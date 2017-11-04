package kotNES.mapper

import kotNES.Cartridge

class NROM(private var cartridge: Cartridge) {
    private fun toPrgROMAddress(address: Int): Int {
        return (address - 0x8000) % 0x4000
    }

    fun read(address: Int): Int = when {
        address >= 0x8000 -> cartridge.readPRGRom(toPrgROMAddress(address))
        address <= 0x2000 -> cartridge.readCHRRom(address)
        else -> 0
    }

    fun write(address: Int, value: Int) = when(address) {
        in 0x0000..0x1FFF -> cartridge.writeCHRRom(address, value)
        else -> Unit
    }
}