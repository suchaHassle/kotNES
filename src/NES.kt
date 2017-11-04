package kotNES

fun main(args: Array<String>) {
    var cartridge = Cartridge("nestest.nes")
    var memory = Memory(cartridge)
    var cpu = CPU(memory)
    cpu.reset()
    var i: Int = 0

    while(i < 24000) {
        cpu.tick()
        i++
    }
}