package kotNES

import palette

private const val gameWidth = 256
private const val gameHeight = 240
private const val scanlineCount = 261
private const val cyclesPerLine = 341

class PPU(private var emulator: Emulator) {
    var ppuMemory = PpuMemory(emulator)
    var bitMap = IntArray(gameWidth * gameHeight)
    var spritePositions = IntArray(8)
    var spritePatterns = IntArray(8)
    var spritePriorities = IntArray(8)
    var spriteIndexes = IntArray(8)
    var tileShiftRegister: Long = 0

    private var bufferPos = 0
    private var cycle = 0
    private var spriteCount = 0
    private var scanline = 0

    fun reset() {
        cycle = 340
        scanline = 240
        bitMap.fill(0)
        spritePositions.fill(0)
    }

    fun step() {
        bitMap.fill(0)
        bufferPos = 0

        val renderingEnabled = ppuMemory.ppuFlags.showBackground || ppuMemory.ppuFlags.showSprites

        // Scanline
        val preLine = scanline == 261
        val visibleLine = scanline < 240
        val postLine = scanline == 240
        val renderLine = preLine || visibleLine

        // Cycle
        val prefetchCycle = cycle in 321..336
        val visibleCycle = cycle in 1..256
        val fetchCycle = prefetchCycle || visibleCycle

        if (renderingEnabled) {
            if (visibleLine && visibleCycle) renderPixel()
        }
    }

    private var ppuClocksSinceVBL = 0

    fun tick() {

    }

    private fun renderPixel() {
        val x = cycle - 1
        val y = scanline
        val background = if (x < 8 && !ppuMemory.ppuFlags.showLeftBackground) 0 else backgroundPixel()
        val b = background % 4 != 0
        val (i, spritePixel) = spritePixel()
        val s = spritePixel % 4 != 0

        val color: Int = when {
            !b && !s -> 0
            !b && s -> spritePixel or 0x10
            b && !s -> background
            else -> {
                if (spriteIndexes[i] == 0 && x < 255) ppuMemory.ppuFlags.spriteZeroHit = true
                if (spritePriorities[i] == 0) (spritePixel and 0x10)
                else background
            }
        }

        val c = palette(ppuMemory.readPalleteRam(color) % 64)
        bitMap[x*y] = c
    }

    private fun spritePixel(): Pair<Int, Int> = when {
        !ppuMemory.ppuFlags.showSprites -> Pair(0,0)
        else -> {
            for (i in 0..(spriteCount - 1)) {
                var offset = (cycle - 1) - spritePositions[i]
                if (offset in 0..7) {
                    offset = 7 - offset
                    val color = (spritePatterns[i] shr ((offset*4) and 0xFF))
                    if (color % 4 != 0)
                        Pair(i, color)
                }
            }
            Pair(0,0)
        }
    }

    private fun backgroundPixel(): Int = when {
        !ppuMemory.ppuFlags.showBackground -> 0
        else -> ((tileShiftRegister shr 32) as Int shr ((7 - ppuMemory.ppuFlags.X) * 4)) and 0x0F
    }
}