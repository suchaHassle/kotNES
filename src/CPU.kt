package kotNES

import isBitSet
import toSignedByte
import toSignedShort
import toUnsignedInt

class CPU(memory: Memory) {
    var registers = Register()
    var statusFlags = StatusFlag()
    var opcodes = Opcodes()
    var cycles: Int = 0
    var pageCycled: Boolean = false

    var memory: Memory = memory

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

    private var instructionCycles: IntArray = intArrayOf(
     // 0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
        7,  6,  2,  8,  3,  3,  5,  5,  3,  2,  2,  2,  4,  4,  6,  6, // 0
        2,  5,  2,  8,  4,  4,  6,  6,  2,  4,  2,  7,  4,  4,  7,  7, // 1
        6,  6,  2,  8,  3,  3,  5,  5,  4,  2,  2,  2,  4,  4,  6,  6, // 2
        2,  5,  2,  8,  4,  4,  6,  6,  2,  4,  2,  7,  4,  4,  7,  7, // 3
        6,  6,  2,  8,  3,  3,  5,  5,  3,  2,  2,  2,  3,  4,  6,  6, // 4
        2,  5,  2,  8,  4,  4,  6,  6,  2,  4,  2,  7,  4,  4,  7,  7, // 5
        6,  6,  2,  8,  3,  3,  5,  5,  4,  2,  2,  2,  5,  4,  6,  6, // 6
        2,  5,  2,  8,  4,  4,  6,  6,  2,  4,  2,  7,  4,  4,  7,  7, // 7
        2,  6,  2,  6,  3,  3,  3,  3,  2,  2,  2,  2,  4,  4,  4,  4, // 8
        2,  6,  2,  6,  4,  4,  4,  4,  2,  5,  2,  5,  5,  5,  5,  5, // 9
        2,  6,  2,  6,  3,  3,  3,  3,  2,  2,  2,  2,  4,  4,  4,  4, // A
        2,  5,  2,  5,  4,  4,  4,  4,  2,  4,  2,  4,  4,  4,  4,  4, // B
        2,  6,  2,  8,  3,  3,  5,  5,  2,  2,  2,  2,  4,  4,  6,  6, // C
        2,  5,  2,  8,  4,  4,  6,  6,  2,  4,  2,  7,  4,  4,  7,  7, // D
        2,  6,  2,  8,  3,  3,  5,  5,  2,  2,  2,  2,  4,  4,  6,  6, // E
        2,  5,  2,  8,  4,  4,  6,  6,  2,  4,  2,  7,  4,  4,  7,  7  // F
    )

    private var instructionPagedCycles: IntArray = intArrayOf(
     // 0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, // 0
        1,  1,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  1,  0,  0, // 1
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, // 2
        1,  1,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  1,  0,  0, // 3
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, // 4
        1,  1,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  1,  0,  0, // 5
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, // 6
        1,  1,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  1,  0,  0, // 7
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, // 8
        1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, // 9
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, // A
        1,  1,  0,  1,  0,  0,  0,  0,  0,  1,  0,  1,  1,  1,  1,  1, // B
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, // C
        1,  1,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  1,  0,  0, // D
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, // E
        1,  1,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  1,  0,  0  // F
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
            var opcode = memory.read(i)
            println("Executing opcode: " + opcode.toString())
            pageCycled = false

            opcodes.opcode[opcode]?.also {
                it.op(this)
            }

            i += instructionSizes[opcode]
            cycles += instructionCycles[opcode]
            if (pageCycled) { cycles += instructionPagedCycles[opcode] }
            registers.tick(instructionSizes[opcode])
        }
    }

    fun reset() {
        registers.reset()
        statusFlags.reset()
    }
}

data class Register (
        private var _A: Int = 0,
        private var _X: Int = 0,
        private var _Y: Int = 0,
        private var _S: Int = 0,
        private var _P: Int = 0,
        private var _PC: Int = 0
) {
    var A: Int
        get()= _A and 0xFF
        set(value) { _A = value and 0xFF }

    var X: Int
        get() = _X and 0xFF
        set(value) { _X = value and 0xFF }

    var Y: Int
        get() = _Y and 0xFF
        set(value) { _Y = value and 0xFF }

    var S: Int
        get() = _S and 0xFF
        set(value) { _S = value and 0xFF }

    var P: Int
        get() = _P and 0xFF
        set(value) { _P = value and 0xFF }

    var PC: Int
        get() = _PC and 0xFF
        set(value) { _PC = value and 0xFF }

    fun reset() {
        A = 0
        X = 0
        Y = 0
        S = 0xFD
        P = 0
        PC = 0xC000
    }

    fun tick(count: Int) {
        PC += count
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

    fun setZn(value: Int) {
        Zero = (value == 0)
        Negative = ((value shr 7) and 1) == 1
    }
}
