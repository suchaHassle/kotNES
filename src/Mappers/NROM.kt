package kotNES.mapper

import kotNES.Cartridge
import kotNES.Emulator
import kotNES.Mapper

class NROM(emulator: Emulator) : Mapper(emulator) {
    private var cartridge: Cartridge = emulator.cartridge

    init {
        mirroringType = if (cartridge.mirroringMode == 1) VramMirroring.Vertical else VramMirroring.Horizontal
    }

    private fun toPrgROMAddress(address: Int): Int =
            if (cartridge.data[4] == 1.toByte()) ((address - 0x8000) % 0x4000) else (address - 0x8000)

    override fun read(address: Int): Int = when {
        address >= 0x8000 -> cartridge.readPRGRom(toPrgROMAddress(address))
        address <= 0x2000 -> cartridge.readCHRRom(address)
        else -> 0
    }

    override fun write(address: Int, value: Int) = when(address) {
        in 0x0000..0x1FFF -> cartridge.writeCHRRom(address, value)
        else -> Unit
    }
}