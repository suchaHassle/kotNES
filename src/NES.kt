package kotNES

class NES {
    private lateinit var cpu: CPU
    private lateinit var memory: Memory

    fun main(args: Array<String>) {
        var cartridge = Cartridge("xyene.nes")
        memory = Memory(cartridge)
        cpu = CPU(memory)

        while (true) {
            cpu.tick()
        }
    }
}