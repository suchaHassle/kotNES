package kotNES

import isBitSet
import toSignedByte
import toUnsignedInt

class Opcodes {
    val opcode = HashMap<Int, Opcode>()

    private enum class AddressMode {
        Absolute,
        AbsoluteX,
        AbsoluteY,
        Accumulator,
        Immediate,
        Implied,
        IndexedIndirect,
        Indirect,
        IndirectIndexed,
        Relative,
        ZeroPage,
        ZeroPageX,
        ZeroPageY
    }

    init {
        /* LSR Opcodes */
        opcode[0x4A]
        opcode[0x46]
        opcode[0x56]
        opcode[0x4E]
        opcode[0x5E]

        /* ROL Opcodes */
        opcode[0x2A] = rol(AddressMode.Accumulator, accumulator())
        opcode[0x26] = rol(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x36] = rol(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0x2E] = rol(AddressMode.Absolute, absolute())
        opcode[0x3E] = rol(AddressMode.AbsoluteX, absoluteX())

        /* ROR Opcodes */
        opcode[0x6A] = ror(AddressMode.Accumulator, accumulator())
        opcode[0x66] = ror(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x76] = ror(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0x6E] = ror(AddressMode.Absolute, absolute())
        opcode[0x7E] = ror(AddressMode.AbsoluteX, absoluteX())
    }

    private fun absolute(): (CPU) -> Int = {
        it.memory.read16(it.registers.PC + 1)
    }

    private fun absoluteX(): (CPU) -> Int = {
        (it.memory.read16(it.registers.PC + 1) + it.registers.X)
    }

    private fun accumulator(): (CPU) -> Int = { 0 }

    private fun zeroPageAdr(): (CPU) -> Int = {
        it.memory.read(it.registers.PC + 1).toUnsignedInt()
    }

    private fun zeroPageXAdr(): (CPU) -> Int = {
        ((it.memory.read(it.registers.PC + 1).toUnsignedInt() + it.registers.X) and 0xFF)
    }

    private fun zeroPageYAdr(): (CPU) -> Int = {
        ((it.memory.read(it.registers.PC + 1).toUnsignedInt() + it.registers.Y) and 0xFF)
    }

    private fun rol(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val tempCarry = statusFlags.Carry
            var address = address(it)

            if (mode == AddressMode.Accumulator) {
                statusFlags.Carry = registers.A.isBitSet(7)
                registers.A = ((registers.A.toUnsignedInt() shl 1) or (if (tempCarry) 1 else 0)).toSignedByte()

                statusFlags.SetZn(registers.A)
            } else {
                var data = memory.read(address)
                statusFlags.Carry = data.isBitSet(7)

                data = ((data.toUnsignedInt() shl 1) or (if (tempCarry) 1 else 0)).toSignedByte()
                memory.write(address, data)

                statusFlags.SetZn(data)
            }
        }
    }

    private fun ror(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val tempCarry = statusFlags.Carry
            var address = address(it)

            if (mode == AddressMode.Accumulator) {
                statusFlags.Carry = registers.A.isBitSet(0)
                registers.A = ((registers.A.toUnsignedInt() shr 1) or (if (tempCarry) 0x80 else 0)).toSignedByte()

                statusFlags.SetZn(registers.A)
            } else {
                var data = memory.read(address)
                statusFlags.Carry = data.isBitSet(0)

                data = ((data.toUnsignedInt() shr 1) or (if (tempCarry) 0x80 else 0)).toSignedByte()
                memory.write(address, data)

                statusFlags.SetZn(data)
            }
        }
    }

    private fun lsr(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            var address = address(it)

            if (mode == AddressMode.Accumulator) {
                statusFlags.Carry = (registers.A.toUnsignedInt() and 1) == 1
                registers.A = (registers.A.toUnsignedInt() shr 1).toSignedByte()

                statusFlags.SetZn(registers.A)
            } else {
                var data = memory.read(address)
                statusFlags.Carry = (data.toUnsignedInt() and 1) == 1

                memory.write(address, (data.toUnsignedInt() shr 1).toSignedByte())

                statusFlags.SetZn(data)
            }
        }
    }
}


class Opcode(val op: (CPU) -> Unit)