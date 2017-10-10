package kotNES

import isBitSet
import toSignedByte
import toSignedShort
import toUnsignedInt

class CPU(memory: Memory) {
    var registers = Register()
    var statusFlags = StatusFlag()
    var opcodes = Opcodes()

    var memory: Memory = memory
    private var A: Byte = 0 // Accumulator

    private var addressModes: IntArray = intArrayOf(
     // 0    1   2   3   4     5    6    7   8   9   A   B   C   D   E   F
         6,  7,  6,  7,  11,  11,  11,  11,  6,  5,  4,  5,  1,  1,  1,  1, // 0
        10,  9,  6,  9,  12,  12,  12,  12,  6,  3,  6,  3,  2,  2,  2,  2, // 1
         1,  7,  6,  7,  11,  11,  11,  11,  6,  5,  4,  5,  1,  1,  1,  1, // 2
        10,  9,  6,  9,  12,  12,  12,  12,  6,  3,  6,  3,  2,  2,  2,  2, // 3
         6,  7,  6,  7,  11,  11,  11,  11,  6,  5,  4,  5,  1,  1,  1,  1, // 4
        10,  9,  6,  9,  12,  12,  12,  12,  6,  3,  6,  3,  2,  2,  2,  2, // 5
         6,  7,  6,  7,  11,  11,  11,  11,  6,  5,  4,  5,  8,  1,  1,  1, // 6
        10,  9,  6,  9,  12,  12,  12,  12,  6,  3,  6,  3,  2,  2,  2,  2, // 7
         5,  7,  5,  7,  11,  11,  11,  11,  6,  5,  6,  5,  1,  1,  1,  1, // 8
        10,  9,  6,  9,  12,  12,  13,  13,  6,  3,  6,  3,  2,  2,  3,  3, // 9
         5,  7,  5,  7,  11,  11,  11,  11,  6,  5,  6,  5,  1,  1,  1,  1, // A
        10,  9,  6,  9,  12,  12,  13,  13,  6,  3,  6,  3,  2,  2,  3,  3, // B
         5,  7,  5,  7,  11,  11,  11,  11,  6,  5,  6,  5,  1,  1,  1,  1, // C
        10,  9,  6,  9,  12,  12,  12,  12,  6,  3,  6,  3,  2,  2,  2,  2, // D
         5,  7,  5,  7,  11,  11,  11,  11,  6,  5,  6,  5,  1,  1,  1,  1, // E
        10,  9,  6,  9,  12,  12,  12,  12,  6,  3,  6,  3,  2,  2,  2,  2  // F
    )

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

    fun start() {
        var i = 0

        while (true) {
            var opcode = memory.read(i).toUnsignedInt()
            println("Executing opcode: " + opcode.toString())

            opcodes.opcode[opcode]?.also {
                it.op(this)
            }

            i += instructionSizes[opcode]
            registers.tick(instructionSizes[opcode])
        }
    }

    fun reset() {
        registers.reset()
        statusFlags.reset()
    }
}

data class Register (
        var A: Byte = 0,
        var X: Byte = 0,
        var Y: Byte = 0,
        var S: Byte = 0,
        var P: Byte = 0,
        var PC: Short = 0
) {
    fun reset() {
        A = 0
        X = 0
        Y = 0
        S = 0xFD.toSignedByte()
        P = 0
        PC = 0xC000.toSignedShort()
    }

    fun tick(count: Int) {
        PC = (PC + count).toSignedShort()
    }
}

data class StatusFlag (
        var Carry: Boolean = false,
        var Zero: Boolean = true,
        var InterruptDisable: Boolean = true,
        var DecimalMode: Boolean = false,
        var BreakCommand: Boolean = false,
        var Overflow: Boolean = false,
        var Negative: Boolean = false
) {
    fun reset() {
        Carry = false
        Zero = false
        InterruptDisable = true
        DecimalMode = false
        BreakCommand = false
        Overflow = false
        Negative = false
    }

    fun asByte() =
            ((if (Negative) (1 shl 7) else 0) or
                    (if (Overflow) (1 shl 6) else 0) or
                    (1 shl 5) or // Special logic needed for the B flag...
                    (0 shl 4) or
                    (if (DecimalMode) (1 shl 3) else 0) or
                    (if (InterruptDisable) (1 shl 2) else 0) or
                    (if (Zero) (1 shl 1) else 0) or
                    (if (Carry) 1 else 0)).toSignedByte()

    fun toFlags(status: Byte) {
        Carry = status.isBitSet(0)
        Zero = status.isBitSet(1)
        InterruptDisable = status.isBitSet(2)
        DecimalMode = status.isBitSet(3)
        Overflow = status.isBitSet(6)
        Negative = status.isBitSet(7)
    }

    fun setZn(value: Byte) {
        Zero = (value.toUnsignedInt() == 0)
        Negative = ((value.toUnsignedInt() shr 7) and 1) == 1
    }
}
