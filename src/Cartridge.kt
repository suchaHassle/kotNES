package kotNES

import toUnsignedInt
import java.io.FileInputStream

class Cartridge(filePath: String) {
    private val MAGIC_HEADER = 0x1A53454E

    private var stream = FileInputStream(filePath)
    var data = ByteArray(stream.available())

    val prgROMSize: Int
    val chrROMSize: Int
    private val flag6: Int
    private val flag7: Int
    private val flag9: Int
    private val prgRAMSize: Int
    val mapper: Int

    val mirroringMode: Int

    var prgROM: IntArray
    lateinit var chrROM: IntArray
    var prgRam = IntArray(0x2000)

    class InvalidROM(override var message: String) : Exception()
    class NoCHRRomException(override var message: String) : Exception()

    init {
        // Parse Header
        stream.read(data, 0, data.size)

        if (java.nio.ByteBuffer.wrap(data.copyOfRange(0,16)).order(
                java.nio.ByteOrder.LITTLE_ENDIAN).int != MAGIC_HEADER)
            throw InvalidROM("Invalid ROM Signature")

        prgROMSize = data[4].toUnsignedInt() * 0x4000
        chrROMSize = data[5].toUnsignedInt() * 0x2000
        prgRAMSize = data[8].toUnsignedInt() * 0x2000

        flag6 = data[6].toUnsignedInt()
        flag7 = data[7].toUnsignedInt()
        flag9 = data[9].toUnsignedInt()

        mirroringMode = if (flag6 and 0x1 > 0) 1 else 0

        mapper = data[6].toUnsignedInt() shr(4) or (data[7].toUnsignedInt()  and 0xF0)

        // Loading Prg ROM
        val prgOffset = 16 + if ((flag6 and 0b100) != 0) 512 else 0
        prgROM = data.copyOfRange(16, prgOffset + prgROMSize).map { it.toUnsignedInt() }.toIntArray()

        if (chrROMSize != 0) chrROM = data.copyOfRange(prgOffset + prgROMSize, prgOffset +
                prgROMSize + chrROMSize).map { it.toUnsignedInt() }.toIntArray()
    }

    fun readPRGRom(address: Int): Int = prgROM[address]

    fun readCHRRom(address: Int): Int = when {
        chrROMSize != 0 -> chrROM[address] and 0xFF
        else -> throw NoCHRRomException("There's no CHR ROM")
    }

    fun writeCHRRom(address: Int, value: Int) { chrROM[address] = value }

    fun writePRGRam(address: Int, value: Int) { prgRam[address] = value }

    override fun toString(): String = "ROM Size: ${prgROM.size}, VROM Size: ${chrROM.size}\nMapper: $mapper"
}
