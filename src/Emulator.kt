package kotNES

class Emulator {
    var cartridge = Cartridge("nestest.nes")
    var memory = CpuMemory(this)
    var cpu = CPU(memory)
    var ppu = PPU(this)

    fun start() {
        cpu.reset()
    }

    fun stepSeconds(seconds: Float) {
        var cycles = (cpu.cpuFrequency * seconds) as Int
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
}