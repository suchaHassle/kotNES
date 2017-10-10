package kotNES

import kotNES.mapper.NROM
import toUnsignedInt

/**
 * $0000-0800 - Internal RAM, 2KB chip in the NES
 * $2000-2007 - PPU access ports
 * $4000-4017 - Audio and controller access ports
 * $6000-7FFF - Optional WRAM inside the game cart
 * $8000-FFFF - Game cartridge ROM
 */

class Memory(cartridge: Cartridge) {
    var mapper: NROM = NROM(cartridge)

    class NotImplementedException(override var message: String) : Exception()

    private val internalRam = ByteArray(0x800)

    fun read(address: Int): Byte = when (address) {
        in 0x000..0x1FFF -> internalRam[address and 0x800]
        in 0x4020..0xFFFF -> mapper.read(address)
        else -> throw NotImplementedException("Only internal RAM Address right now")
    }

    fun read16(address: Int): Int {
        val lo = read(address).toUnsignedInt()
        val hi = read(address + 1).toUnsignedInt()
        return (hi shl 8) or lo
    }

    fun read16wrap(address: Int): Int {
        if (address and 0xFF == 0xFF) {
            val lo = read(address).toUnsignedInt()
            val hi = read(address and 0xFF.inv()).toUnsignedInt()

            return (hi shl 8) or lo
        }
        return read16(address)
    }

    fun write(address: Int, value: Byte) {
        when (address) {
            in 0x000..0x1FFF -> internalRam[address and 0x800] = value
            else -> throw NotImplementedException("Only internal RAM Address right now")
        }
    }
}
