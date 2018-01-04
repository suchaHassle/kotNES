package kotNES

import palette

private const val gameWidth = 256
private const val gameHeight = 240
private const val scanlineCount = 261
private const val cyclesPerLine = 341

class PPU(private var emulator: Emulator) {
    var ppuMemory = PpuMemory(emulator)
    var ppuFlags = ppuMemory.ppuFlags
    var bitMap = IntArray(gameWidth * gameHeight)
    var spritePositions = IntArray(8)
    var spritePatterns = IntArray(8)
    var spritePriorities = IntArray(8)
    var spriteIndexes = IntArray(8)
    var tileShiftRegister: Long = 0

    private var attributeTableByte = 0
    private var bufferPos = 0
    private var cycle = 0
    private var highTileByte = 0
    private var lowTileByte = 0
    private var nametableByte = 0
    private var spriteCount = 0
    private var scanline = 0
    private var tileData: Long = 0

    fun reset() {
        cycle = 340
        scanline = 240
        bitMap.fill(0)
        spritePositions.fill(0)
    }

    fun step() {
        bitMap.fill(0)
        bufferPos = 0

        val renderingEnabled = ppuFlags.showBackground || ppuFlags.showSprites

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
            // Background Logic
            if (visibleLine && visibleCycle) renderPixel()
            if (renderLine && fetchCycle) {
                tileShiftRegister = tileShiftRegister shl 4

                when (cycle % 8) {
                    1 -> fetchNametableByte()
                    3 -> fetchAttributeTableByte()
                    5 -> fetchTileByte(false)
                    7 -> fetchTileByte(true)
                    0 -> storeTileData()
                }
            }

            if (preLine && cycle in 280..304) copyY()

            if (renderLine) {
                if (fetchCycle && cycle % 8 == 0) incrementX()
                if (cycle == 256) incrementY()
                if (cycle == 257) copyX()
            }
        }
    }

    private fun fetchNametableByte() {
        nametableByte = ppuMemory[0x2000 or (ppuFlags.V and 0x0FFF)]
    }

    private fun fetchAttributeTableByte() {
        val v = ppuFlags.V
        val address = 0x23C0 or (v and 0x0C00) or ((v shr 4) and 0x38) or ((v shr 2) and 0x07)
        val shift = ((v shr 4) and 4) or (v and 2)
        attributeTableByte = ((ppuMemory[address] shr shift) and 3) shl 2
    }

    private fun fetchTileByte(hi: Boolean) {
        val fineY = (ppuFlags.V shr 12) and 7
        val address = (0x1000 * ppuFlags.patternTableAddress) + (16 * nametableByte) + fineY

        if (hi) highTileByte = ppuMemory[address + 8] and 0xFFFF
        else lowTileByte = ppuMemory[address] and 0xFFFF
    }

    private fun storeTileData() {
        var data: Int = 0
        for (i in 0..7) {
            val p1 = (lowTileByte and 0x80) shr 7
            val p2 = (highTileByte and 0x80) shr 6
            lowTileByte = (lowTileByte shl 1) and 0xFFFF
            highTileByte = (highTileByte shl 1) and 0xFFFF
            data = data shl 4
            data = data or (attributeTableByte or p1 or p2)
        }
        tileData = data as Long
    }

    fun tick() {

    }

    private fun renderPixel() {
        val x = cycle - 1
        val y = scanline
        val background = if (x < 8 && !ppuFlags.showLeftBackground) 0 else backgroundPixel()
        val b = background % 4 != 0
        val (i, spritePixel) = spritePixel()
        val s = spritePixel % 4 != 0

        val color: Int = when {
            !b && !s -> 0
            !b && s -> spritePixel or 0x10
            b && !s -> background
            else -> {
                if (spriteIndexes[i] == 0 && x < 255) ppuFlags.spriteZeroHit = true
                if (spritePriorities[i] == 0) (spritePixel and 0x10)
                else background
            }
        }

        val c = palette(ppuMemory.readPalleteRam(color) % 64)
        bitMap[x*y] = c
    }

    private fun spritePixel(): Pair<Int, Int> = when {
        !ppuFlags.showSprites -> Pair(0,0)
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
        !ppuFlags.showBackground -> 0
        else -> ((tileShiftRegister shr 32) as Int shr ((7 - ppuFlags.X) * 4)) and 0x0F
    }

    private fun copyX() {
        ppuFlags.V = (ppuFlags.V and 0xFBE0) or (ppuFlags.T and 0x041F)
    }

    private fun copyY() {
        ppuFlags.V = (ppuFlags.V and 0x841F) or (ppuFlags.T and 0x7BE0)
    }

    private fun incrementX() {
        if (ppuFlags.V and 0x001F == 31)
            ppuFlags.V = (ppuFlags.V and 0xFFE0) xor 0x0400
        else
            ppuFlags.V++
    }

    private fun incrementY() {
        if (ppuFlags.V and 0x7000 != 0x7000)
            ppuFlags.V += 0x1000
        else {
            ppuFlags.V = ppuFlags.V and 0x8FFF
            var y = (ppuFlags.V and 0x03E0) shr 5
            when (y) {
                29 -> {
                    y = 0
                    ppuFlags.V = ppuFlags.V xor 0x0800
                }
                31 -> y = 0
                else -> y++
            }

            ppuFlags.V = (ppuFlags.V and 0xFC1F) or (y shl 5)
        }
    }
}