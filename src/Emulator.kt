package kotNES

import kotNES.mapper.MMC3
import kotNES.mapper.NROM
import kotNES.mapper.UxROM
import java.lang.Long.max
import java.nio.file.Path

class Emulator(path: Path) {
    var cartridge = Cartridge(path)
    var memory = CpuMemory(this)
    var cpu = CPU(memory)
    var ppu = PPU(this)
    var controller = Controller()
    var mapper: Mapper
    var evenOdd: Boolean = false
    val codeExecutionThread = Thread(Runnable { this.start() })
    lateinit var display: HeavyDisplayPanel

    init {
        when (cartridge.mapper) {
            0 -> mapper = NROM(this)
            2 -> mapper = UxROM(this)
            4 -> mapper = MMC3(this)
            else -> throw UnsupportedMapper("${cartridge.mapper} mapper is not supported")
        }
    }

    fun start() {
        cpu.reset()
        ppu.reset()

        while (true) {
            val startTime = System.currentTimeMillis()
            stepSeconds()
            val endTime = System.currentTimeMillis()

            var sleepTime: Long = (((1000.0) / 51.5) - (endTime - startTime)).toLong()
            sleepTime = max(sleepTime, 0)

            Thread.sleep(sleepTime)
        }
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
            mapper.step()
        }
    }

    class UnsupportedMapper(s: String) : Throwable(s)
}