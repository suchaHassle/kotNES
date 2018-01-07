import kotNES.CPU
import kotNES.PPU
import kotNES.PpuMemory

/**
 * Credit to @alondero
 */

fun Int.toSignedByte(): Byte {
    if (this > 127) return (this-256).toByte()
    else return this.toByte()
}

fun Byte.toUnsignedInt(): Int {
    if (this < 0) return this+256
    else return this.toInt()
}

fun Int.toSignedShort(): Short {
    if (this > 32767) return (this-65536).toShort()
    else return this.toShort()
}

fun Short.toUnsignedInt(): Int {
    if (this < 0) return this+65536
    else return this.toInt()
}

fun Short.toHexString() = "%04X".format(this.toUnsignedInt()).toUpperCase()
fun Byte.toHexString() = "%02X".format(this.toUnsignedInt()).toUpperCase()
fun Int.toHexString() = "%02X".format(this).toUpperCase()

fun Byte.isBitSet(i: Int) = (this.toUnsignedInt() and (i shl 1)) != 0
fun Int.isBitSet(i: Int) = (this and (1 shl i)) != 0
fun Boolean.asInt() = if (this) 1 else 0
fun Byte.toggleBit(i: Int): Byte = (this.toUnsignedInt() xor (1 shl i)).toSignedByte()
fun Byte.setBit(i: Int): Byte = (this.toUnsignedInt() or (1 shl i)).toSignedByte()
fun Byte.clearBit(i: Int): Byte = (this.toUnsignedInt() and ((1 shl i).inv())).toSignedByte()
fun Byte.letBit(i: Int, on: Boolean): Byte {if (on) {return this.setBit(i)} else return this.clearBit(i)}

fun Byte.shiftRight() = ((this.toUnsignedInt() shr 1) and 0x7F).toSignedByte()

private var instructionSizes: IntArray = intArrayOf(
     // 0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
        1,  2,  0,  0,  2,  2,  2,  0,  1,  2,  1,  0,  3,  3,  3,  0,  // 0
        2,  2,  0,  0,  2,  2,  2,  0,  1,  3,  1,  0,  3,  3,  3,  0,  // 1
        3,  2,  0,  0,  2,  2,  2,  0,  1,  2,  1,  0,  3,  3,  3,  0,  // 2
        2,  2,  0,  0,  2,  2,  2,  0,  1,  3,  1,  0,  3,  3,  3,  0,  // 3
        1,  2,  0,  0,  2,  2,  2,  0,  1,  2,  1,  0,  3,  3,  3,  0,  // 4
        2,  2,  0,  0,  2,  2,  2,  0,  1,  3,  1,  0,  3,  3,  3,  0,  // 5
        1,  2,  0,  0,  2,  2,  2,  0,  1,  2,  1,  0,  3,  3,  3,  0,  // 6
        2,  2,  0,  0,  2,  2,  2,  0,  1,  3,  1,  0,  3,  3,  3,  0,  // 7
        2,  2,  0,  0,  2,  2,  2,  0,  1,  0,  1,  0,  3,  3,  3,  0,  // 8
        2,  2,  0,  0,  2,  2,  2,  0,  1,  3,  1,  0,  0,  3,  0,  0,  // 9
        2,  2,  2,  0,  2,  2,  2,  0,  1,  2,  1,  0,  3,  3,  3,  0,  // A
        2,  2,  0,  0,  2,  2,  2,  0,  1,  3,  1,  0,  3,  3,  3,  0,  // B
        2,  2,  0,  0,  2,  2,  2,  0,  1,  2,  1,  0,  3,  3,  3,  0,  // C
        2,  2,  0,  0,  2,  2,  2,  0,  1,  3,  1,  0,  3,  3,  3,  0,  // D
        2,  2,  0,  0,  2,  2,  2,  0,  1,  2,  1,  0,  3,  3,  3,  0,  // E
        2,  2,  0,  0,  2,  2,  2,  0,  1,  3,  1,  0,  3,  3,  3,  0   // F
)

private var vRamMirror = arrayOf(
        intArrayOf(0, 0, 1, 1),
        intArrayOf(0, 1, 0, 1),
        intArrayOf(0, 0, 0, 0),
        intArrayOf(1, 1, 1, 1),
        intArrayOf(0, 1, 2, 3)
)

private var palette = intArrayOf(
        0x666666, 0x002A88, 0x1412A7, 0x3B00A4, 0x5C007E, 0x6E0040, 0x6C0600, 0x561D00,
        0x333500, 0x0B4800, 0x005200, 0x004F08, 0x00404D, 0x000000, 0x000000, 0x000000,
        0xADADAD, 0x155FD9, 0x4240FF, 0x7527FE, 0xA01ACC, 0xB71E7B, 0xB53120, 0x994E00,
        0x6B6D00, 0x388700, 0x0C9300, 0x008F32, 0x007C8D, 0x000000, 0x000000, 0x000000,
        0xFFFEFF, 0x64B0FF, 0x9290FF, 0xC676FF, 0xF36AFF, 0xFE6ECC, 0xFE8170, 0xEA9E22,
        0xBCBE00, 0x88D800, 0x5CE430, 0x45E082, 0x48CDDE, 0x4F4F4F, 0x000000, 0x000000,
        0xFFFEFF, 0xC0DFFF, 0xD3D2FF, 0xE8C8FF, 0xFBC2FF, 0xFEC4EA, 0xFECCC5, 0xF7D8A5,
        0xE4E594, 0xCFEF96, 0xBDF4AB, 0xB3F3CC, 0xB5EBF2, 0xB8B8B8, 0x000000, 0x000000)

fun CPU.instructionSize(address: Int) = instructionSizes[address]
fun PpuMemory.vRamMirrorLookup(i: Int, j: Int) = vRamMirror[i][j]
fun PPU.palette(address: Int) = palette[address]