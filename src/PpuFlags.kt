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
        var writeToggle: Boolean = false,

        // PPUSCROLL
        var scrollX: Int = 0,
        var scrollY: Int = 0,

        // PPUADDR
        private var _busAddress: Int = 0,

        // PPUDATA
        private var readBuffer: Int = 0,

        var busData: Int = 0,
        var _oamAddress: Int = 0,
        var _lastWrittenRegister: Int = 0,
        var T: Int = 0,
        var X: Int = 0,
        private var _V: Int = 0,
        var ppu: PpuMemory
) {
    var V: Int // 15 Bits
        get() = _V
        set(value) {
            _V = value and 0x7FFF
        }

    var busAddress: Int
        get() = _busAddress
        set(value) { _busAddress = value and 0x3FFF }

    var oamAddress: Int
        get() = _oamAddress
        set(value) { _oamAddress = value and 0xFF }

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
            writeToggle = false
            val ret = (_lastWrittenRegister and 0x1F) or
                    (spriteOverflow.asInt() shl 5) or
                    (spriteZeroHit.asInt() shl 6) or
                    (vBlankStarted.asInt() shl 7)

            vBlankStarted = false
            return ret
        }

    var PPUSCROLL: Int = 0
        set(value) {
            if (writeToggle) {
                scrollY = value
                T = T and 0x8FFF or (value and 0x7 shl 12)
                T = T and 0xFC1F or (value and 0xF8 shl 2)
            } else {
                scrollX = value
                X = value and 0x7
                T = T and 0xFFE0 or (value shr 3)
            }
            writeToggle = writeToggle xor true
        }

    var PPUADDR: Int = 0
        set(value) {
            if (writeToggle) {
                T = (T and 0xFF00) or value
                busAddress = T
                V = T
            } else {
                T = (T and 0x80FF) or ((value and 0x3F) shl 8)
            }
            writeToggle = writeToggle xor true
        }
}