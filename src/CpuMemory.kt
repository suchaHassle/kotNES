package kotNES

import kotNES.mapper.NROM

/**
 * $0000-0800 - Internal RAM, 2KB chip in the NES
 * $2000-2007 - PPU access ports
 * $4000-4017 - Audio and controller access ports
 * $6000-7FFF - Optional WRAM inside the game cart
 * $8000-FFFF - Game cartridge ROM
 */

class CpuMemory(var emulator: Emulator) {
    var cartridge = emulator.cartridge
    var mapper: NROM = NROM(cartridge)

    private var apuIoRegisters = IntArray(0x20)
    private val internalRam = IntArray(0x800)

    operator fun get(address: Int): Int = when (address) {
        in 0x0000..0x1FFF -> internalRam[address % 0x800] and 0xFF
        in 0x2000..0x3FFF -> emulator.ppu.ppuMemory.readRegister(0x2000 + address%8)
        0x4014 -> emulator.ppu.ppuMemory.readRegister(address)
        in 0x4000..0x401F -> apuIoRegisters[address-0x4000]
        in 0x4020..0xFFFF -> mapper.read(address) and 0xFF
        else -> throw IndexOutOfBoundsException("$address is out of bounds")
    }

    fun readWord(address: Int): Int = (get(address + 1) shl 8) or get(address)

    fun readWordWrap(address: Int): Int = when {
        address and 0xFF == 0xFF -> (get(address and 0xFF.inv()) shl 8) or get(address)
        else -> readWord(address)
    }

    operator fun set(address: Int, value: Int) {
        when (address) {
            in 0x0000..0x1FFF -> internalRam[address % 0x800] = value
            in 0x2000..0x3FFF -> emulator.ppu.ppuMemory.writeRegister(0x2000 + address%8, value)
            0x4014 -> emulator.ppu.ppuMemory.writeRegister(address, value)
            in 0x4000..0x401F -> apuIoRegisters[address-0x4000] = value
            in 0x4020..0xFFFF -> mapper.write(address, value)
            else -> throw IndexOutOfBoundsException("$address is out of bounds")
        }
    }
}
