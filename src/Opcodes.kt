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
        /* ASL Opcodes */
        opcode[0x0A] = asl(AddressMode.Accumulator, accumulator())
        opcode[0x06] = asl(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x16] = asl(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0x0E] = asl(AddressMode.Absolute, absolute())
        opcode[0x1E] = asl(AddressMode.AbsoluteX, absoluteX())

        /* LSR Opcodes */
        opcode[0x4A] = lsr(AddressMode.Accumulator, accumulator())
        opcode[0x46] = lsr(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x56] = lsr(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0x4E] = lsr(AddressMode.Absolute, absolute())
        opcode[0x5E] = lsr(AddressMode.AbsoluteX, absoluteX())

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

    private fun absoluteY(): (CPU) -> Int = {
        (it.memory.read16(it.registers.PC + 1) + it.registers.Y)
    }

    private fun accumulator(): (CPU) -> Int = { 0 }

    private fun indirectX(): (CPU) -> Int = { it.memory.read16wrap(indirectXAdr()(it)) }

    private fun indirectY(): (CPU) -> Int = { it.memory.read16wrap(indirectYAdr()(it)) + it.registers.Y }

    private fun indirectXAdr(): (CPU) -> Int = {
        (it.memory.read(it.registers.PC + 1) + it.registers.X) and 0xFF
    }

    private fun indirectYAdr(): (CPU) -> Int = {
        it.memory.read(it.registers.PC + 1).toUnsignedInt()
    }

    private fun immediate(): (CPU) -> Int = { it.registers.PC + 1 }

    private fun implied(): (CPU) -> Int = { 0 }

    private fun relative(): (CPU) -> Int = { it.registers.PC + it.memory.read(it.registers.PC + 1) + 2 }

    private fun zeroPageAdr(): (CPU) -> Int = {
        it.memory.read(it.registers.PC + 1).toUnsignedInt()
    }

    private fun zeroPageXAdr(): (CPU) -> Int = {
        ((it.memory.read(it.registers.PC + 1).toUnsignedInt() + it.registers.X) and 0xFF)
    }

    private fun zeroPageYAdr(): (CPU) -> Int = {
        ((it.memory.read(it.registers.PC + 1).toUnsignedInt() + it.registers.Y) and 0xFF)
    }

    private fun asl(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            var address = address(it)

            if (mode == AddressMode.Accumulator) {
                statusFlags.Carry = registers.A.isBitSet(7)
                registers.A = (registers.A.toUnsignedInt() shl 1).toSignedByte()

                statusFlags.SetZn(registers.A)
            } else {
                var data = memory.read(address)
                statusFlags.Carry = data.isBitSet(7)

                memory.write(address, (data.toUnsignedInt() shl 1).toSignedByte())

                statusFlags.SetZn((data.toUnsignedInt() shl 1).toSignedByte())
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

                statusFlags.SetZn((data.toUnsignedInt() shr 1).toSignedByte())
            }
        }
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
}


class Opcode(val op: (CPU) -> Unit)