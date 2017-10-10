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
        Indirect,
        IndirectX,
        IndirectY,
        Relative,
        ZeroPage,
        ZeroPageX,
        ZeroPageY
    }

    init {
        /* AND Opcodes */
        opcode[0x29] = and(AddressMode.Immediate, immediate())
        opcode[0x25] = and(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x35] = and(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0x2D] = and(AddressMode.Absolute, absolute())
        opcode[0x3D] = and(AddressMode.AbsoluteX, absoluteX())
        opcode[0x39] = and(AddressMode.AbsoluteY, absoluteY())
        opcode[0x21] = and(AddressMode.IndirectX, indirectX())
        opcode[0x31] = and(AddressMode.IndirectY, indirectY())

        /* ASL Opcodes */
        opcode[0x0A] = asl(AddressMode.Accumulator, accumulator())
        opcode[0x06] = asl(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x16] = asl(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0x0E] = asl(AddressMode.Absolute, absolute())
        opcode[0x1E] = asl(AddressMode.AbsoluteX, absoluteX())

        /* BIT Opcodes */
        opcode[0x24] = bit(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x2C] = bit(AddressMode.Absolute, absolute())

        /* CLC, CLD, CLI, CLV Opcode */
        opcode[0x18] = clc(AddressMode.Implied, implied())
        opcode[0xD8] = cld(AddressMode.Implied, implied())
        opcode[0x58] = cli(AddressMode.Implied, implied())
        opcode[0xB8] = clv(AddressMode.Implied, implied())

        /* CMP Opcodes */
        opcode[0xC9] = cmp(AddressMode.Immediate, immediate())
        opcode[0xC5] = cmp(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0xD5] = cmp(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0xCD] = cmp(AddressMode.Absolute, absolute())
        opcode[0xDD] = cmp(AddressMode.AbsoluteX, absoluteX())
        opcode[0xD9] = cmp(AddressMode.AbsoluteY, absoluteY())
        opcode[0xC1] = cmp(AddressMode.IndirectX, indirectX())
        opcode[0xD1] = cmp(AddressMode.IndirectY, indirectY())

        /* CPX Opcodes */
        opcode[0xE0] = cpx(AddressMode.Immediate, immediate())
        opcode[0xE4] = cpx(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0xEC] = cpx(AddressMode.Absolute, absolute())

        /* CPY Opcodes */
        opcode[0xC0] = cpy(AddressMode.Immediate, immediate())
        opcode[0xC4] = cpy(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0xCC] = cpy(AddressMode.Absolute, absolute())

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

    /* Address mode memory address */

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

    /* Opcode methods */

    private fun and(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            var address = address(it)
            it.registers.A = (it.registers.A.toUnsignedInt() and it.memory.read(address).toUnsignedInt()).toSignedByte()
            statusFlags.SetZn(registers.A)
        }
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

    private fun bit(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            var address = address(it)
            var data = it.memory.read(address)

            it.statusFlags.Negative = data.isBitSet(7)
            it.statusFlags.Overflow = data.isBitSet(6)
            it.statusFlags.Zero = (data.toUnsignedInt() and it.registers.A.toUnsignedInt()) == 0
        }
    }

    private fun clc(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.statusFlags.Carry = false
        }
    }

    private fun cld(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.statusFlags.DecimalMode = false
        }
    }

    private fun cli(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.statusFlags.InterruptDisable = false
        }
    }

    private fun clv(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.statusFlags.Overflow = false
        }
    }

    private fun cmp(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            var address = address(it)
            var data = it.memory.read(address)

            it.statusFlags.Carry = it.registers.A >= data
            it.statusFlags.SetZn((it.registers.A - data).toSignedByte())
        }
    }

    private fun cpx(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            var address = address(it)
            var data = it.memory.read(address)

            it.statusFlags.Carry = it.registers.X >= data
            it.statusFlags.SetZn((it.registers.X - data).toSignedByte())
        }
    }

    private fun cpy(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            var address = address(it)
            var data = it.memory.read(address)

            it.statusFlags.Carry = it.registers.Y >= data
            it.statusFlags.SetZn((it.registers.Y - data).toSignedByte())
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