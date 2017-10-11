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
fun Int.isBitSet(i: Int) = (this and (i shl 1)) != 0
fun Byte.toggleBit(i: Int): Byte = (this.toUnsignedInt() xor (1 shl i)).toSignedByte()
fun Byte.setBit(i: Int): Byte = (this.toUnsignedInt() or (1 shl i)).toSignedByte()
fun Byte.clearBit(i: Int): Byte = (this.toUnsignedInt() and ((1 shl i).inv())).toSignedByte()
fun Byte.letBit(i: Int, on: Boolean): Byte {if (on) {return this.setBit(i)} else return this.clearBit(i)}

fun Byte.shiftRight() = ((this.toUnsignedInt() shr 1) and 0x7F).toSignedByte()