package kotNES

class Emulator {
    var cartridge = Cartridge("palette.nes")
    var memory = CpuMemory(this)
    var cpu = CPU(memory)
    var ppu = PPU(this)

    fun start() {
        cpu.reset()
        ppu.reset()
    }

    fun stepSeconds(seconds: Double) {
        var cycles = (cpu.cpuFrequency * seconds).toInt()
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
}