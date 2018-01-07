package kotNES

import instructionSize
import isBitSet
import kotNES.Opcodes.AddressMode
import toHexString

class CPU(var memory: CpuMemory) {
    enum class Interrupts(val value: Int) {
        NMI(0), IRQ(1), RESET(2)
    }

    var registers = Register()
    var statusFlags = StatusFlag()
    private var idleCycles = 0
    private var interruptHandlerOffsets = intArrayOf(0xFFFA, 0xFFFE, 0xFFFC)
    private var interrupts = BooleanArray(2)
    private var opcodes = Opcodes()
    var opcode: Int = 0
    var cycles: Int = 0
    val cpuFrequency = 1789773

    fun tick(): Int {
        if (idleCycles > 0) {
            idleCycles--
            return 1
        }

        for (i in 0..1)
            if (interrupts[i]) {
                pushWord(registers.PC)
                push(registers.P)
                registers.PC = memory.readWord(interruptHandlerOffsets[i])
                statusFlags.InterruptDisable = true
                interrupts[i] = false
            }

        val initCycle = cycles
        opcode = memory[registers.PC]
        registers.P = statusFlags.asByte()
        opcodes.pageCrossed = false
        //println(toString())

        val address: Int = opcodes.getAddress(AddressMode.values()[opcodes.addressModes[opcode] - 1], this)
        registers.tick(instructionSize(opcode))

        opcodes.opcode[opcode].also { it.op(this, address) }

        return cycles - initCycle
    }

    fun reset() {
        registers.reset()
        registers.PC = memory.readWord(0xFFFC)
        //registers.PC = 0xC000
        statusFlags.reset()

        idleCycles = 0
        cycles = 0
    }

    override fun toString(): String {
        return registers.PC.toHexString() + "  " + opcode.toHexString() +
                "        " + registers.toString() + " "
    }

    fun push(data: Int) { memory[0x100 or registers.S--] = data }

    fun pushWord(data: Int) {
        push(data shr 8)
        push(data and 0xFF)
    }

    fun pop(): Int = memory[0x100 or ++registers.S]

    fun popWord(): Int = pop() or (pop() shl 8)

    fun triggerInterrupt(type: Interrupts) {
        if (!statusFlags.InterruptDisable || type == Interrupts.NMI)
            interrupts[type.value] = true
    }

    fun addIdleCycles(i: Int) {
        idleCycles += i
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

    override fun toString(): String =
            "A:${A.toHexString()} " + "X:${X.toHexString()} " + "Y:${Y.toHexString()} " +
                "P:${P.toHexString()} " + "SP:${S.toHexString()}"
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
        toFlags(0x24)
    }

    fun asByte() =
            ((if (Negative) (1 shl 7) else 0) or
                    (if (Overflow) (1 shl 6) else 0) or
                    (1 shl 5) or // Special logic needed for the B flag...
                    (if (BreakCommand) (1 shl 4) else 0) or
                    (if (DecimalMode) (1 shl 3) else 0) or
                    (if (InterruptDisable) (1 shl 2) else 0) or
                    (if (Zero) (1 shl 1) else 0) or
                    (if (Carry) 1 else 0))

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
