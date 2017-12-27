package kotNES

import asInt
import isBitSet

data class PpuFlags (
        // PPUCTRL register flags
        var vramIncrement: Int = 0,
        var spriteTableAddress: Int = 0,
        var patternTableAddress: Int = 0,
        var spriteSize: Boolean = false,
        var isMaster: Boolean = false,
        var nmiOutput: Boolean = false,

        // PPUMASK register flags
        var grayscale: Boolean = false,
        var showLeftBackground: Boolean = false,
        var showLeftSprites: Boolean = false,
        var showBackground: Boolean = false,
        var showSprites: Boolean = false,
        var emphasizeRed: Boolean = false,
        var emphasizeGreen: Boolean = false,
        var emphasizeBlue: Boolean = false,

        // PPUSTATUS
        var spriteOverflow: Boolean = false,
        var spriteZeroHit: Boolean = false,
        var vBlankStarted: Boolean = false,
        var addressLatch: Boolean = false,

        // PPUSCROLL
        var scrollX: Int = 0,
        var scrollY: Int = 0,

        // PPUADDR
        var busAddress: Int = 0,

        var _lastWrittenRegister: Int = 0,
        var T: Int = 0,
        var X: Int = 0,
        private var _V: Int = 0

) {
    var V: Int // 15 Bits
        get() = _V
        set(value) {
            _V = value and 0x7FFF
        }


    /*
        7  bit  0
        ---- ----
        VPHB SINN
        |||| ||||
        |||| ||++- Base nametable address
        |||| ||    (0 = $2000; 1 = $2400; 2 = $2800; 3 = $2C00)
        |||| |+--- VRAM address increment per CPU read/write of PPUDATA
        |||| |     (0: add 1, going across; 1: add 32, going down)
        |||| +---- Sprite pattern table address for 8x8 sprites
        ||||       (0: $0000; 1: $1000; ignored in 8x16 mode)
        |||+------ Background pattern table address (0: $0000; 1: $1000)
        ||+------- Sprite size (0: 8x8; 1: 8x16)
        |+-------- PPU master/slave select
        |          (0: read backdrop from EXT pins; 1: output color on EXT pins)
        +--------- Generate an NMI at the start of the
                   vertical blanking interval (0: off; 1: on)
    */
    var PPUCTRL: Int = 0
        set(value) {
            vramIncrement = if (value.isBitSet(2)) 32 else 1
            spriteTableAddress = if (value.isBitSet(3)) 0x1000 else 0
            patternTableAddress = if (value.isBitSet(4)) 0x1000 else 0
            spriteSize = value.isBitSet(5)
            isMaster = value.isBitSet(6)
            nmiOutput = value.isBitSet(7)

            T = (T and 0xF3FF) or ((value and 0x3) shl 10)
        }


    /*
        7  bit  0
        ---- ----
        BGRs bMmG
        |||| ||||
        |||| |||+- Greyscale (0: normal color, 1: produce a greyscale display)
        |||| ||+-- 1: Show background in leftmost 8 pixels of screen, 0: Hide
        |||| |+--- 1: Show sprites in leftmost 8 pixels of screen, 0: Hide
        |||| +---- 1: Show background
        |||+------ 1: Show sprites
        ||+------- Emphasize red*
        |+-------- Emphasize green*
        +--------- Emphasize blue*
    */
    var PPUMASK: Int = 0
        set(value) {
            grayscale = value.isBitSet(0)
            showLeftBackground = value.isBitSet(1)
            showLeftSprites = value.isBitSet(2)
            showBackground = value.isBitSet(3)
            showSprites = value.isBitSet(4)
            emphasizeRed = value.isBitSet(5)
            emphasizeGreen = value.isBitSet(6)
            emphasizeBlue = value.isBitSet(7)
        }

    var PPUSTATUS: Int = 0
        get() {
            addressLatch = false
            val ret = (_lastWrittenRegister and 0x1F) or
                    (spriteOverflow.asInt() shl 5) or
                    (spriteZeroHit.asInt() shl 6) or
                    (vBlankStarted.asInt() shl 7)

            vBlankStarted = false
            return ret
        }

    var PPUSCROLL: Int = 0
        set(value) {
            if (addressLatch) {
                scrollY = value
                T = T and 0x8FFF or (value and 0x7 shl 12)
                T = T and 0xFC1F or (value and 0xF8 shl 2)
            } else {
                scrollX = value
                X = value and 0x7
                T = T and 0xFFE0 or (value shr 3)
            }
            addressLatch = addressLatch xor true
        }

    var PPUADDR: Int = 0
        set(value) {
            if (addressLatch) {
                T = (T and 0xFF00) or value
                busAddress = T
                V = T
            } else {
                T = (T and 0x80FF) or ((value and 0x3F) shl 8)
            }
            addressLatch = addressLatch xor true
        }
}