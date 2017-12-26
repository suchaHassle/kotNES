package kotNES

class Emulator {
    var cartridge = Cartridge("nestest.nes")
    var memory = Memory(cartridge)
    var cpu = CPU(memory)
    var ppu = PPU(memory)

    fun start() {
        cpu.reset()

        while(true) {
            cpu.tick()
        }
    }
}