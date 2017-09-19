package kotNES.mapper

import kotNES.Cartridge

class NROM(cartridge: Cartridge) {

    private var cartridge: Cartridge = cartridge

    fun read(address: Int): Byte = when {
        address >= 0x8000 -> cartridge.readPRGRom(address)
        address <= 0x2000 -> cartridge.readCHRRom(address)
        else -> 0
    }

    fun write(address: Int) {
        // Nothing for now
    }
}