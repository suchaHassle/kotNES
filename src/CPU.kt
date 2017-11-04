package kotNES

import isBitSet
import toSignedByte
import toSignedShort
import toUnsignedInt

class CPU(memory: Memory) {
    var registers = Register()
    var statusFlags = StatusFlag()
    var opcodes = Opcodes()
    var opcode: Int = 0
    var cycles: Int = 0

    var memory: Memory = memory

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

    class IllegalOpcode(override var message: String) : Exception()

    fun tick(): Int {
        val initCycle = cycles
        opcode = memory.read(registers.PC)
        registers.P = statusFlags.asByte()
        println(String.format("%4s", java.lang.Integer.toHexString(registers.PC).toUpperCase()).replace(' ', '0')
        + "  " + String.format("%2s", java.lang.Integer.toHexString(opcode).toUpperCase()).replace(' ', '0')
        + "        " + registers.toString())
        opcodes.pageCrossed = false

        opcodes.opcode[opcode].also {
            it.op(this)
            it.
        }

        return cycles - initCycle
    }

    fun reset() {
        registers.reset()
        //registers.PC = memory.read16(0xFFFC)
        registers.PC = 0xC000
        statusFlags.reset()

        cycles = 0
    }

    fun increment(cycle: Int) {
        if (cycle == -1 && instructionSizes[opcode] == 0) throw IllegalOpcode("${java.lang.Integer.toHexString(opcode)} is not a legal opcode")

        cycles += cycle
        registers.tick(instructionSizes[opcode])
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
        get() = _PC and 0xFFFF
        set(value) { _PC = value and 0xFFFF}

    fun reset() {
        A = 0
        X = 0
        Y = 0
        S = 0xFD
        P = 0
    }

    fun tick(count: Int) {
        PC += count
    }

    override fun toString(): String {
        return "A: ${String.format("%2s", java.lang.Integer.toHexString(A).toUpperCase()).replace(' ', '0')} " +
                "X: ${String.format("%2s", java.lang.Integer.toHexString(X).toUpperCase()).replace(' ', '0')} " +
                "Y: ${String.format("%2s", java.lang.Integer.toHexString(Y).toUpperCase()).replace(' ', '0')} " +
                "P: ${String.format("%2s", java.lang.Integer.toHexString(P).toUpperCase()).replace(' ', '0')} " +
                "SP: ${String.format("%2s", java.lang.Integer.toHexString(S).toUpperCase()).replace(' ', '0')}"
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
                    (if (DecimalMode) (1 shl 3) else 0) or
                    (if (InterruptDisable) (1 shl 2) else 0) or
                    (if (Zero) (1 shl 1) else 0) or
                    (if (Carry) 1 else 0)) and 0xFF

    fun toFlags(status: Int) {
        Carry = status.isBitSet(0)
        Zero = status.isBitSet(1)
        InterruptDisable = status.isBitSet(2)
        DecimalMode = status.isBitSet(3)
        BreakCommand = status.isBitSet(4)
        Overflow = status.isBitSet(6)
        Negative = status.isBitSet(7)
    }

    fun setZn(value: Int) {
        val value = value and 0xFF
        Zero = (value == 0)
        Negative = ((value shr 7) and 1) == 1
    }
}
