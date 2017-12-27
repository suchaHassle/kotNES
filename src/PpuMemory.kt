package kotNES

class PpuMemory {
    var ppuFlags = PpuFlags()

    operator fun get(address: Int) = when (address) {
        0 -> ppuFlags._lastWrittenRegister and 0xFF
        1 -> ppuFlags._lastWrittenRegister and 0xFF
        2 -> ppuFlags.PPUSTATUS and 0xFF
        5 -> ppuFlags._lastWrittenRegister and 0xFF
        6 -> ppuFlags._lastWrittenRegister and 0xFF
        else -> IllegalAccessError("This address is illegal $address")
    }
}