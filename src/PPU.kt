package kotNES

import palette

private const val gameWidth = 256
private const val gameHeight = 240
private const val scanlineCount = 261
private const val cyclesPerLine = 341

class PPU(private var emulator: Emulator) {
    var ppuMemory = PpuMemory(emulator)
    var bitMap = IntArray(gameWidth * gameHeight)
    var tileShiftRegister: Long = 0
    private var bufferPos = 0
    private var priority = IntArray(gameWidth * gameHeight)

    fun step() {
        bitMap.fill(0)
        priority.fill(0)
        bufferPos = 0

        for (scanline in -1..(scanlineCount -1))
            for (cycle in 0..(cyclesPerLine - 1))
                tick(scanline, cycle)
    }

    private var ppuClocksSinceVBL = 0

    fun tick(scanline: Int, cycle: Int) {
        val renderCycle = cycle in 1..256
        val prefetchCycle = cycle in 321..336
        val fetchCycle = renderCycle or prefetchCycle

        if (ppuMemory.ppuFlags.vBlankStarted) ppuClocksSinceVBL++

        if (scanline in 0..239 || scanline == -1) {
            if (renderCycle) renderPixel(cycle - 1, scanline)
        }
    }

    fun renderPixel(x: Int, y: Int) {

    }

    fun renderBGPixel(cycle: Int, scanline: Int) {
        if (!ppuMemory.ppuFlags.showLeftBackground && cycle < 8 ||
                !ppuMemory.ppuFlags.showBackground && scanline != -1) {
            bitMap[bufferPos] = palette(ppuMemory[0x3F00 + (if (ppuMemory.ppuFlags.busAddress and 0x3F00 === 0x3F00)
                ppuMemory.ppuFlags.busAddress and 0x001F else 0)] and 0x3F)
            return
        }

        if (scanline != -1) {
            var paletteEntry = ((tileShiftRegister shr 32 shr ((7 - ppuMemory.ppuFlags.X) * 4)) and 0xF) as Int
            if (paletteEntry % 4 == 0) paletteEntry = 0

            priority[bufferPos] = paletteEntry
            bitMap[bufferPos] = palette(ppuMemory[0x3F00 + paletteEntry] and 0x3F)
        }
    }
}