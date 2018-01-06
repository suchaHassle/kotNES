package kotNES

import vRamMirrorLookup

class PpuMemory(private var emulator: Emulator) {
    var ppuFlags = PpuFlags(memory=this)
    var vRam = IntArray(2048)
    var paletteRam = IntArray(32)
    var oam = IntArray(256)

    fun readRegister(address: Int): Int = when (address) {
        //0x2000 -> ppuFlags._lastWrittenRegister and 0xFF
        //1 -> ppuFlags._lastWrittenRegister and 0xFF
        0x2002 -> ppuFlags.PPUSTATUS and 0xFF
        //3 -> ppuFlags.OAMADDR and 0xFF
        0x2004 -> ppuFlags.OAMDATA and 0xFF
        //5 -> ppuFlags._lastWrittenRegister and 0xFF
        //6 -> ppuFlags._lastWrittenRegister and 0xFF
        0x2007 -> ppuFlags.PPUDATA and 0xFF
        else -> throw IllegalAccessError("$address is not a valid address")
    }

    fun writeRegister(address: Int, value: Int) {
        ppuFlags._lastWrittenRegister = value and 0xFF
        when (address) {
            0x2000 -> ppuFlags.PPUCTRL = value
            0x2001 -> ppuFlags.PPUMASK = value
            0x4014 -> writeOamDma(value)
            0x2003 -> ppuFlags.OAMADDR = value
            0x2004 -> ppuFlags.OAMDATA = value
            0x2005 -> ppuFlags.PPUSCROLL = value
            0x2006 -> ppuFlags.PPUADDR = value
            0x2007 -> ppuFlags.PPUDATA = value
            else -> throw IllegalAccessError("$address is not a valid address")
        }
    }

    private fun writeOamDma(value: Int) {
        var startAddr = value shl 8
        for (i in 0..255) {
            oam[ppuFlags.oamAddress] = emulator.cpu.memory[startAddr]
            ppuFlags.oamAddress++
            startAddr++
        }

        emulator.cpu.addIdleCycles(513 + if (emulator.cpu.cycles % 2 == 1) 1 else 0)
    }

    fun readPaletteRam(address: Int) = paletteRam[if (address >= 16 && address % 4 == 0) address - 16 else address]

    operator fun get(address: Int) = when (address) {
        in 0x0000..0x1FFF -> emulator.memory[address]
        in 0x2000..0x2FFF -> vRam[getVramMirror(address)] and 0xFF
        in 0x3000..0x3EFF -> vRam[getVramMirror(address - 0x1000)] and 0xFF
        in 0x3F00..0x3FFF -> paletteRam[(getPaletteRamIndex(address) - 0x3F00) and 0x1F]
        else -> throw IllegalAccessError("$address is not a valid address")
    }

    operator fun set(address: Int, value: Int) = when (address) {
        in 0x0000..0x1FFF -> emulator.memory[address] = value
        in 0x2000..0x2FFF -> vRam[getVramMirror(address)] = value and 0xFF
        in 0x3000..0x3EFF -> vRam[getVramMirror(address - 0x1000)] = value and 0xFF
        in 0x3F00..0x3FFF -> paletteRam[(getPaletteRamIndex(address) - 0x3F00) and 0x1F] = value
        else -> throw IllegalAccessError("$address is not a valid address")
    }

    private fun getVramMirror(address: Int): Int {
        return vRamMirrorLookup(emulator.cartridge.mirroringMode, ((address - 0x2000).div(0x400))) * 0x400 +
                ((address - 0x2000).rem(0x400))
    }

    private fun getPaletteRamIndex(address: Int): Int = when (address) {
        0x3F10, 0x3F14, 0x3F18, 0x3F0C -> address - 0x10
        else -> address
    }
}