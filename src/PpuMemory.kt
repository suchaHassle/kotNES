package kotNES

class PpuMemory {
    var ppuFlags = PpuFlags()

    operator fun get(address: Int) = when (address) {
        0 -> ppuFlags._lastWrittenRegister and 0xFF
        1 -> ppuFlags._lastWrittenRegister and 0xFF
        2 -> ppuFlags.PPUSTATUS and 0xFF
        5 -> ppuFlags._lastWrittenRegister and 0xFF
        6 -> ppuFlags._lastWrittenRegister and 0xFF
        else -> IllegalAccessError("This address is illegal, $address")
    }

    operator fun set(address: Int, value: Int) {
        ppuFlags._lastWrittenRegister = value and 0xFF
        when (address) {
            0 -> ppuFlags.PPUCTRL = value
            1 -> ppuFlags.PPUMASK = value
            2 -> return
            5 -> ppuFlags.PPUSCROLL = value
            6 -> ppuFlags.PPUADDR = value
        }
    }
}