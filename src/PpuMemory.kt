package kotNES

import vRamMirrorLookup

class PpuMemory(private var emulator: Emulator) {
    var ppuFlags = PpuFlags(memory=this)
    var vRam = IntArray(2048)
    var paletteRam = IntArray(32)
    var oam = IntArray(256)

    fun readRegister(address: Int): Int = when ((address and 0x7) - 0x2000) {
        0 -> ppuFlags._lastWrittenRegister and 0xFF
        1 -> ppuFlags._lastWrittenRegister and 0xFF
        2 -> ppuFlags.PPUSTATUS and 0xFF
        3 -> ppuFlags.OAMADDR and 0xFF
        4 -> ppuFlags.OAMDATA and 0xFF
        5 -> ppuFlags._lastWrittenRegister and 0xFF
        6 -> ppuFlags._lastWrittenRegister and 0xFF
        7 -> ppuFlags.PPUDATA and 0xFF
        else -> throw IllegalAccessError("$address is not a valid address")
    }

    fun writeRegister(address: Int, value: Int) {
        ppuFlags._lastWrittenRegister = value and 0xFF
        when ((address and 0x7) - 0x2000) {
            0 -> ppuFlags.PPUCTRL = value
            1 -> ppuFlags.PPUMASK = value
            2 -> return
            3 -> ppuFlags.OAMADDR = value
            4 -> ppuFlags.OAMDATA = value
            5 -> ppuFlags.PPUSCROLL = value
            6 -> ppuFlags.PPUADDR = value
            7 -> ppuFlags.PPUDATA = value
            else -> throw IllegalAccessError("$address is not a valid address")
        }
    }

    operator fun get(address: Int) = when (address) {
        in 0x0000..0x1FFF -> emulator.memory[address]
        in 0x2000..0x2FFF -> vRam[getVramMirror(address)] and 0xFF
        in 0x3000..0x3EFF -> vRam[getVramMirror(address - 0x1000)] and 0xFF
        in 0x3F00..0x3FFF -> paletteRam[((if (address == 0x3F10 || address == 0x3F14 || address == 0x3F18 || address == 0x3F0C)
            address - 0x10 else address) - 0x3F00) and 0x1F]
        else -> throw IllegalAccessError("$address is not a valid address")
    }

    operator fun set(address: Int, value: Int) = when (address) {
        in 0x0000..0x1FFF -> emulator.memory[address] = value
        in 0x2000..0x2FFF -> vRam[getVramMirror(address)] = value and 0xFF
        in 0x3000..0x3EFF -> vRam[getVramMirror(address - 0x1000)] = value and 0xFF
        in 0x3F00..0x3FFF -> paletteRam[((if (address == 0x3F10 || address == 0x3F14 || address == 0x3F18 || address == 0x3F0C)
            address - 0x10 else address) - 0x3F00) and 0x1F] = value
        else -> throw IllegalAccessError("$address is not a valid address")
    }

    private fun getVramMirror(address: Int): Int {
        return vRamMirrorLookup(emulator.cartridge.mirroringMode, ((address - 0x2000).div(0x400))) * 0x400 +
                ((address - 0x2000).rem(0x400))
    }
}