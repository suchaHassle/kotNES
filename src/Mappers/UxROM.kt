package kotNES.mapper

import kotNES.Emulator
import kotNES.Mapper

class UxROM(emulator: Emulator) : Mapper(emulator) {
    var bankOffset = 0

    override fun read(address: Int): Int = when(address) {
        in 0x6000..0x7FFF -> emulator.cartridge.readPRGRam(address - 0x6000)
        in 0x8000..0xBFFF -> emulator.cartridge.readPRGRom(bankOffset + (address - 0x8000))
        in 0xC000..0xFFFF -> emulator.cartridge.readPRGRom(emulator.cartridge.prgROM.size + address - 0x10000)
        else -> throw IndexOutOfBoundsException("$address is out of bounds")
    }

    override fun write(address: Int, value: Int) {
        when(address) {
            in 0x6000..0x7FFF -> emulator.cartridge.writePRGRam(address -0x6000, value)
            in 0x8000..0xFFFF -> bankOffset = (value and 0xF) * 0x4000
        }
    }
}