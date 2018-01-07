package kotNES

import kotNES.mapper.NROM

class Emulator {
    var cartridge = Cartridge("roms/palette.nes")
    var memory = CpuMemory(this)
    var cpu = CPU(memory)
    var ppu = PPU(this)
    var mapper: Mapper

    init {
        when (cartridge.mapper) {
            0 -> mapper = NROM(this)
            else -> throw UnsupportedMapper("${cartridge.mapper} mapper is not supported")
        }
    }

    fun start() {
        cpu.reset()
        ppu.reset()
    }

    fun stepSeconds(seconds: Double) {
        var cycles = (cpu.cpuFrequency * seconds).toLong()
        while (cycles > 0)
            cycles -= step()
    }

    private fun step(): Int {
        val cpuCycles = cpu.tick()
        val ppuCycles = cpuCycles * 3
        for (i in 0..(ppuCycles - 1)) {
            ppu.step()
        }

        return cpuCycles
    }

    fun addFrameListener(frameListener: FrameListener) {
        ppu.addFrameListener(frameListener)
    }

    class UnsupportedMapper(s: String) : Exception()
}