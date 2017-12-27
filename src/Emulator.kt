package kotNES

class Emulator {
    var cartridge = Cartridge("nestest.nes")
    var memory = CpuMemory(this)
    var cpu = CPU(memory)
    var ppu = PPU(this)

    fun start() {
        cpu.reset()

        while(true) {
            cpu.tick()
        }
    }
}