package kotNES

import kotNES.mapper.NROM

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

    private val internalRam = IntArray(0x800)

    fun read(address: Int): Int = when (address) {
        in 0x000..0x1FFF -> internalRam[address % 0x800] and 0xFF
        in 0x4020..0xFFFF -> mapper.read(address) and 0xFF
        else -> throw NotImplementedException("Only internal RAM Address right now: " + address)
    }

    fun readWord(address: Int): Int = (read(address + 1) shl 8) or read(address)

    fun readWordWrap(address: Int): Int = when {
        address and 0xFF == 0xFF -> (read(address and 0xFF.inv()) shl 8) or read(address)
        else -> readWord(address)
    }

    fun write(address: Int, value: Int) {
        when (address) {
            in 0x0000..0x1FFF -> internalRam[address % 0x800] = value
            in 0x4020..0xFFFF -> mapper.write(address, value)
            else -> throw NotImplementedException("Only internal RAM Address right now")
        }
    }
}
