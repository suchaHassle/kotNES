package kotNES

fun main(args: Array<String>) {
    var cartridge = Cartridge("nestest.nes")
    var memory = Memory(cartridge)
    var cpu = CPU(memory)
    cpu.reset()

    while(true) {
        cpu.tick()
    }
}