package kotNES

import toUnsignedInt
import java.io.FileInputStream

class Cartridge(filePath: String) {

    private val MAGIC_HEADER = 0x1A53454E

    private var stream = FileInputStream(filePath)
    private var data = ByteArray(stream.available())

    private val prgROMSize: Int
    private val chrROMSize: Int
    private val flag6: Int
    private val flag7: Int
    private val flag9: Int
    private val prgRAMSize: Int
    private val mapper: Int

    private var prgROM: IntArray
    private lateinit var chrROM: IntArray

    init {
        // Parse Header
        stream.read(data, 0, data.size)

        if (java.nio.ByteBuffer.wrap(data.copyOfRange(0,4)).order(java.nio.ByteOrder.LITTLE_ENDIAN).int != MAGIC_HEADER)
            throw InvalidROM("Invalid ROM Signature")

        prgROMSize = data[4].toUnsignedInt() and 0xff
        chrROMSize = data[5].toUnsignedInt() and 0xff
        prgRAMSize = data[8].toUnsignedInt()

        flag6 = data[6].toUnsignedInt()
        flag7 = data[7].toUnsignedInt()
        flag9 = data[9].toUnsignedInt()

        mapper = data[6].toUnsignedInt()  shr(4) or (data[7].toUnsignedInt()  and 0xF0)

        // Loading Prg ROM
        var prgOffset = 16 + if ((flag6 and 0x1) > 0) 512 else 0
        prgROM = data.copyOfRange(prgOffset, prgOffset + prgROMSize).map { it.toInt() }.toIntArray()

        if (chrROMSize != 0) chrROM = data.copyOfRange(prgOffset + prgROMSize, prgOffset + prgROMSize + chrROMSize).map { it.toInt() }.toIntArray()
    }

    fun readPRGRom(address: Int): Int {
        return prgROM[address] and 0xFF
    }

    fun readCHRRom(address: Int): Int {
        return if (chrROMSize != 0) chrROM[address] and 0xFF
        else throw NoCHRRomException("There's no CHR ROM")
    }

    override fun toString(): String {
        return "ROM Size: ${prgROM.size}, VROM Size: ${chrROM.size}\nMapper: $mapper"
    }
}

class InvalidROM(override var message: String) : Exception()
class NoCHRRomException(override var message: String) : Exception()
