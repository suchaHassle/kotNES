package kotNES

import palette

private const val gameWidth = 256
private const val gameHeight = 240

class PPU(private var emulator: Emulator) {
    var ppuMemory = PpuMemory(emulator)
    var ppuFlags = ppuMemory.ppuFlags
    var bitMap = IntArray(gameWidth * gameHeight)
    var frame: Long = 0
    var spritePositions = IntArray(8)
    var spritePatterns = IntArray(8)
    var spritePriorities = IntArray(8)
    var spriteIndexes = IntArray(8)
    var tileShiftRegister: Long = 0

    private var attributeTableByte = 0
    private var cycle = 0
    private var highTileByte = 0
    private var listener : FrameListener? = null
    private var lowTileByte = 0
    private var nametableByte = 0
    private var spriteCount = 0
    private var scanline = 0

    fun reset() {
        cycle = 340
        scanline = 240
        frame = 0
        ppuFlags.PPUCTRL = 0
        ppuFlags.PPUMASK = 0
        ppuFlags.oamAddress = 0
    }

    fun step() {
        tick()

        val renderingEnabled = ppuFlags.showBackground || ppuFlags.showSprites

        // Scanline
        val preLine = scanline == 261
        val visibleLine = scanline < 240
        val postLine = scanline == 0
        val renderLine = preLine || visibleLine

        // Cycle
        val prefetchCycle = cycle in 321..336
        val visibleCycle = cycle in 1..256
        val fetchCycle = prefetchCycle || visibleCycle

        if (preLine && cycle == 1) {
            ppuFlags.vBlankStarted = false
            ppuFlags.spriteZeroHit = false
            ppuFlags.spriteOverflow = false
        }

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

            // Sprite Logic
            if (cycle == 257) {
                if (renderLine) evaluateSprites()
                else spriteCount = 0
            }
        }
    }

    private fun tick() {
        if (scanline == 241 && cycle == 1) {
            ppuFlags.vBlankStarted = true
            if (ppuFlags.nmiOutput) emulator.cpu.triggerInterrupt(CPU.Interrupts.NMI)
        }

        val renderingEnabled = ppuFlags.showBackground || ppuFlags.showSprites

        if (renderingEnabled) {
            if (scanline == 261 && ppuFlags.F && cycle == 339) {
                cycle = 0
                scanline = 0
                frame++
                ppuFlags.F = !ppuFlags.F
                listener?.frameUpdate(bitMap)
                return
            }
        }

        cycle++
        if (cycle > 340) {
            cycle = 0
            scanline++
            if (scanline > 261) {
                listener?.frameUpdate(bitMap)
                scanline = 0
                frame++
                ppuFlags.F = !ppuFlags.F
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

    private fun fetchSprite(i: Int, row: Int): Int {
        var row = row
        var tile = ppuMemory.oam[i*4 + 1]
        var attributes = ppuMemory.oam[i*4 + 2]
        val address: Int
        val table: Int
        var data = 0

        if (ppuFlags.spriteSize) {
            if (attributes and 0x80 == 0x80) row = 15 - row
            table = (tile and 1) * 0x1000
            tile = tile and 0xFE
            if (row > 7) {
                tile++
                row -= 8
            }
        } else {
            if (attributes and 0x80 == 0x80) row = 7 - row
            table = ppuFlags.spriteTableAddress
        }

        address = ((table and 0xFFFF) + (16 * (tile and 0xFFFF)) + (row and 0xFFFF)) and 0xFFFF
        val a = (attributes and 3) shl 2
        lowTileByte = ppuMemory[address] and 0xFF
        highTileByte = ppuMemory[address + 8] and 0xFF

        for (i in 0..7) {
            val p1: Int
            val p2: Int

            if (attributes and 0x40 == 0x40) {
                p1 = (lowTileByte and 1) shl 0 and 0xFF
                p2 = (highTileByte and 1) shl 1 and 0xFF
                lowTileByte = lowTileByte shr 1
                highTileByte = highTileByte shr 1
            } else {
                p1 = (lowTileByte and 0x80) ushr 7
                p2 = (highTileByte and 0x80) ushr 6
                lowTileByte = (lowTileByte shl 1) and 0xFF
                highTileByte = (highTileByte shl 1) and 0xFF
            }

            data = (data shl 4) or (a or p1 or p2)
        }

        return data
    }

    private fun fetchTileByte(hi: Boolean) {
        val fineY = (ppuFlags.V shr 12) and 7
        val address = ((ppuFlags.patternTableAddress) + (16 * nametableByte) + fineY) and 0xFFFF

        if (hi) highTileByte = ppuMemory[address + 8] and 0xFF
        else lowTileByte = ppuMemory[address] and 0xFF
    }

    private fun storeTileData() {
        var data: Long = 0
        for (i in 0..7) {
            val p1 = (lowTileByte and 0x80) shr 7
            val p2 = (highTileByte and 0x80) shr 6
            lowTileByte = (lowTileByte shl 1)
            highTileByte = (highTileByte shl 1)
            data = (data shl 4) or (attributeTableByte or p1 or p2).toLong()
        }
        tileShiftRegister = tileShiftRegister or data
    }

    private fun evaluateSprites() {
        var h: Int = if (ppuFlags.spriteSize) 16 else 8
        var count = 0

        for (i in 0..63) {
            val y = ppuMemory.oam[i*4] and 0xFF
            val a = ppuMemory.oam[i*4 + 2] and 0xFF
            val x = ppuMemory.oam[i*4 + 3] and 0xFF
            val row = scanline - y

            if (row in 0..(h-1)) {
                if (count < 8) {
                    spritePatterns[count] = fetchSprite(i, row)
                    spritePositions[count] = x
                    spritePriorities[count] = (a shr 5) and 1
                    spriteIndexes[count] = i
                }
                count++
            }
        }

        if (count > 8) {
            count = 8
            ppuFlags.spriteOverflow = true
        }

        spriteCount = count
    }

    private fun renderPixel() {
        val x = cycle - 1
        val y = scanline
        val background = if (x < 8 && !ppuFlags.showLeftBackground) 0 else backgroundPixel()
        var (i, spritePixel) = spritePixel()
        if (x < 8 && !ppuFlags.showLeftSprites) spritePixel = 0
        val b = background % 4 != 0
        val s = spritePixel % 4 != 0

        val color: Int
        when {
            !b && !s -> color = 0
            !b && s -> color = spritePixel or 0x10
            b && !s -> color = background
            else -> {
                if (spriteIndexes[i] == 0 && x < 255) ppuFlags.spriteZeroHit = true
                color = if (spritePriorities[i] == 0) (spritePixel or 0x10) else background
            }
        }

        val c = palette(ppuMemory.readPaletteRam(color) % 64)
        bitMap[y*256 + x] = c
    }

    private fun spritePixel(): Pair<Int, Int> {
        if (!ppuFlags.showSprites) return Pair(0,0)
        else  {
            for (i in 0..(spriteCount - 1)) {
                var offset = (cycle - 1) - spritePositions[i]
                if (offset in 0..7) {
                    offset = 7 - offset
                    val color = (spritePatterns[i] ushr ((offset * 4) and 0xFF)) and 0x0F
                    if (color % 4 != 0) return Pair(i, color)
                }
            }
            return Pair(0,0)
        }
    }

    private fun backgroundPixel(): Int = when {
        !ppuFlags.showBackground -> 0
        else -> (((tileShiftRegister ushr 32) ushr ((7 - ppuFlags.X) * 4)) and 0x0F).toInt() and 0xFF
    }

    private fun copyX() {
        ppuFlags.V = (ppuFlags.V and 0xFBE0) or (ppuFlags.T and 0x041F)
    }

    private fun copyY() {
        ppuFlags.V = (ppuFlags.V and 0x841F) or (ppuFlags.T and 0x7BE0)
    }

    private fun incrementX() {
        ppuFlags.V = if (ppuFlags.V and 0x001F == 31) ((ppuFlags.V and 0xFFE0) xor 0x0400) else (ppuFlags.V + 1)
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

    fun addFrameListener(frameListener: FrameListener) {
        listener = frameListener
    }
}