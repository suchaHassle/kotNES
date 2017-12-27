import kotNES.CPU
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
        intArrayOf(0, 1, 2, 3),
        intArrayOf(0, 0, 0, 0),
        intArrayOf(1, 1, 1, 1)
)

fun CPU.instructionSize(address: Int) = instructionSizes[address]
fun PpuMemory.vRamMirrorLookup(i: Int, j: Int) = vRamMirror[i][j]