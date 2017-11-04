package kotNES

fun main(args: Array<String>) {
    var cartridge = Cartridge("nestest.nes")
    var memory = Memory(cartridge)
    var cpu = CPU(memory)
    cpu.reset()
    var i: Int = 0

    while(true) {
        cpu.tick()
    }
}