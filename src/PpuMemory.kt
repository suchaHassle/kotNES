package kotNES

import vRamMirrorLookup

class PpuMemory(private var emulator: Emulator) {
    var ppuFlags = PpuFlags(memory=this, emulator=emulator)
    var vRam = IntArray(2048)
    var paletteRam = IntArray(32)
    var oam = IntArray(256)

    fun readRegister(address: Int): Int = when (address) {
        //0x2000 -> ppuFlags._lastWrittenRegister and 0xFF
        //1 -> ppuFlags._lastWrittenRegister and 0xFF
        0x2002 -> ppuFlags.PPUSTATUS
        //3 -> ppuFlags.OAMADDR and 0xFF
        0x2004 -> ppuFlags.OAMDATA
        //5 -> ppuFlags._lastWrittenRegister and 0xFF
        //6 -> ppuFlags._lastWrittenRegister and 0xFF
        0x2007 -> ppuFlags.PPUDATA
        else -> throw IllegalAccessError("$address is not a valid address")
    }

    fun writeRegister(address: Int, value: Int) {
        ppuFlags._lastWrittenRegister = value and 0xFF
        when (address) {
            0x2000 -> ppuFlags.PPUCTRL = value
            0x2001 -> ppuFlags.PPUMASK = value
            0x4014 -> writeDma(value)
            0x2003 -> ppuFlags.OAMADDR = value
            0x2004 -> ppuFlags.OAMDATA = value
            0x2005 -> ppuFlags.PPUSCROLL = value
            0x2006 -> ppuFlags.PPUADDR = value
            0x2007 -> ppuFlags.PPUDATA = value
            else -> throw IllegalAccessError("$address is not a valid address")
        }
    }

    private fun writeDma(value: Int) {
        var startAddr = value shl 8
        for (i in 0..0xFF) {
            oam[ppuFlags.oamAddress++] = emulator.cpu.memory[startAddr++]
        }

        emulator.cpu.addIdleCycles(513 + emulator.cpu.cycles % 2)
    }

    fun readPaletteRam(address: Int): Int = paletteRam[if (address >= 16 && address % 4 == 0) (address - 16) else address]

    private fun writePaletteRam(address: Int, value: Int) {
        paletteRam[if (address >= 16 && address % 4 == 0) (address - 16) else address] = value
    }

    operator fun get(address: Int) = when (address % 0x4000) {
        in 0x0000..0x1FFF -> emulator.mapper.read(address)
        in 0x2000..0x2FFF -> vRam[getVramMirror(address)]
        in 0x3000..0x3EFF -> vRam[getVramMirror(address - 0x1000)]
        in 0x3F00..0x3FFF -> readPaletteRam(address % 32)
        else -> throw IllegalAccessError("$address is not a valid address")
    }

    operator fun set(address: Int, value: Int) = when (address % 0x4000) {
        in 0x0000..0x1FFF -> emulator.mapper.write(address, value)
        in 0x2000..0x2FFF -> vRam[getVramMirror(address)] = value and 0xFFFF
        in 0x3000..0x3EFF -> vRam[getVramMirror(address - 0x1000)] = value and 0xFFFF
        in 0x3F00..0x3FFF -> writePaletteRam(address % 32, value)
        else -> throw IllegalAccessError("$address is not a valid address")
    }

    private fun getVramMirror(address: Int): Int = vRamMirrorLookup(emulator.cartridge.mirroringMode,
            ((address - 0x2000) / 0x400)) * 0x400 + ((address - 0x2000) % 0x400)
}