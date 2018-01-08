package kotNES

import kotNES.mapper.NROM

class Emulator {
    var cartridge = Cartridge("roms/donkeykong.nes")
    var memory = CpuMemory(this)
    var cpu = CPU(memory)
    var ppu = PPU(this)
    var controller = Controller()
    var mapper: Mapper
    var evenOdd: Boolean = false

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

    fun stepSeconds() {
        val orig = evenOdd
        while (orig == evenOdd) step()
    }

    fun step() {
        val cpuCycles = cpu.tick()
        val ppuCycles = cpuCycles * 3
        for (i in 0..(ppuCycles - 1)) {
            ppu.step()
        }
    }

    fun addFrameListener(frameListener: FrameListener) {
        ppu.addFrameListener(frameListener)
    }

    class UnsupportedMapper(s: String) : Exception()
}