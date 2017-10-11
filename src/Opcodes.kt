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

        /* DEC Opcodes */
        opcode[0xC6] = dec(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0xD6] = dec(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0xCE] = dec(AddressMode.Absolute, absolute())
        opcode[0xDE] = dec(AddressMode.AbsoluteX, absoluteX())

        /* DEX, DEY Opcodes */
        opcode[0xCA] = dex(AddressMode.Implied, implied())
        opcode[0x88] = dey(AddressMode.Implied, implied())

        /* EOR Opcodes */
        opcode[0x49] = eor(AddressMode.Immediate, immediate())
        opcode[0x45] = eor(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x55] = eor(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0x4D] = eor(AddressMode.Absolute, absolute())
        opcode[0x5D] = eor(AddressMode.AbsoluteX, absoluteX())
        opcode[0x59] = eor(AddressMode.AbsoluteY, absoluteY())
        opcode[0x41] = eor(AddressMode.IndirectX, indirectX())
        opcode[0x51] = eor(AddressMode.IndirectY, indirectY())

        /* INC, INX, INY Opcodes */
        opcode[0xE6] = inc(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0xF6] = inc(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0xEE] = inc(AddressMode.Absolute, absolute())
        opcode[0xFE] = inc(AddressMode.AbsoluteX, absoluteX())
        opcode[0xE8] = inx(AddressMode.Implied, implied())
        opcode[0xC8] = iny(AddressMode.Implied, implied())

        /* JMP Opcodes */
        opcode[0x4C] = jmp(AddressMode.Absolute, absolute())
        opcode[0x6C] = jmp(AddressMode.Indirect, indirect())

        /* LDA Opcodes */
        opcode[0xA9] = lda(AddressMode.Immediate, immediate())
        opcode[0xA5] = lda(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0xB5] = lda(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0xAD] = lda(AddressMode.Absolute, absolute())
        opcode[0xBD] = lda(AddressMode.AbsoluteX, absoluteX())
        opcode[0xB9] = lda(AddressMode.AbsoluteY, absoluteY())
        opcode[0xA1] = lda(AddressMode.IndirectX, indirectX())
        opcode[0xB1] = lda(AddressMode.IndirectY, indirectY())

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

    private fun indirect(): (CPU) -> Int = { it.memory.read16wrap(it.memory.read16(it.registers.PC + 1)) }

    private fun indirectX(): (CPU) -> Int = { it.memory.read16wrap(indirectXAdr()(it)) }

    private fun indirectY(): (CPU) -> Int = { it.memory.read16wrap(indirectYAdr()(it)) + it.registers.Y }

    private fun indirectXAdr(): (CPU) -> Int = {
        (it.memory.read(it.registers.PC + 1) + it.registers.X) and 0xFF
    }

    private fun indirectYAdr(): (CPU) -> Int = {
        it.memory.read(it.registers.PC + 1)
    }

    private fun immediate(): (CPU) -> Int = { it.registers.PC + 1 }

    private fun implied(): (CPU) -> Int = { 0 }

    private fun relative(): (CPU) -> Int = { it.registers.PC + it.memory.read(it.registers.PC + 1) + 2 }

    private fun zeroPageAdr(): (CPU) -> Int = {
        it.memory.read(it.registers.PC + 1)
    }

    private fun zeroPageXAdr(): (CPU) -> Int = {
        ((it.memory.read(it.registers.PC + 1) + it.registers.X) and 0xFF)
    }

    private fun zeroPageYAdr(): (CPU) -> Int = {
        ((it.memory.read(it.registers.PC + 1) + it.registers.Y) and 0xFF)
    }

    /* Opcode methods */

    private fun and(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val address = address(it)
            it.registers.A = it.registers.A and it.memory.read(address)
            statusFlags.setZn(registers.A)
        }
    }

    private fun asl(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            var address = address(it)

            if (mode == AddressMode.Accumulator) {
                statusFlags.Carry = registers.A.isBitSet(7)
                registers.A = registers.A shl 1

                statusFlags.setZn(registers.A)
            } else {
                var data = memory.read(address)
                statusFlags.Carry = data.isBitSet(7)

                memory.write(address, (data shl 1))

                statusFlags.setZn(data shl 1)
            }
        }
    }

    private fun bit(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val address = address(it)
            var data = it.memory.read(address)

            it.statusFlags.Negative = data.isBitSet(7)
            it.statusFlags.Overflow = data.isBitSet(6)
            it.statusFlags.Zero = data and it.registers.A == 0
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
            val address = address(it)
            val data = it.memory.read(address)

            it.statusFlags.Carry = it.registers.A >= data
            it.statusFlags.setZn(it.registers.A - data)
        }
    }

    private fun cpx(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val address = address(it)
            val data = it.memory.read(address)

            it.statusFlags.Carry = it.registers.X >= data
            it.statusFlags.setZn(it.registers.X - data)
        }
    }

    private fun cpy(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val address = address(it)
            val data = it.memory.read(address)

            it.statusFlags.Carry = it.registers.Y >= data
            it.statusFlags.setZn(it.registers.Y - data)
        }
    }

    private fun dec(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val address = address(it)
            var data = it.memory.read(address)
            data--
            it.memory.write(address, data)
            it.statusFlags.setZn(data)
        }
    }

    private fun dex(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.registers.X--
            it.statusFlags.setZn(it.registers.X)
        }
    }

    private fun dey(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.registers.Y--
            it.statusFlags.setZn(it.registers.Y)
        }
    }

    private fun eor(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val address = address(it)
            val data = it.memory.read(address)
            it.registers.A = (it.registers.A xor data) and 0xFF
        }
    }

    private fun inc(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val address = address(it)
            var data = it.memory.read(address)
            data++
            it.memory.write(address, data)
            it.statusFlags.setZn(data)
        }
    }

    private fun inx(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.registers.X++
            it.statusFlags.setZn(it.registers.X)
        }
    }

    private fun iny(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.registers.Y++
            it.statusFlags.setZn(it.registers.Y)
        }
    }

    private fun jmp(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.registers.PC = address(it)
        }
    }

    private fun lda(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            var address = address(it)

            it.registers.A = it.memory.read(address)
            it.statusFlags.setZn(it.registers.A)
        }
    }

    private fun lsr(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val address = address(it)

            if (mode == AddressMode.Accumulator) {
                statusFlags.Carry = (registers.A and 1) == 1
                registers.A = registers.A shr 1

                statusFlags.setZn(registers.A)
            } else {
                val data = memory.read(address)
                statusFlags.Carry = (data and 1) == 1

                memory.write(address, (data shr 1))

                statusFlags.setZn(data shr 1)
            }
        }
    }

    private fun rol(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val tempCarry = statusFlags.Carry
            val address = address(it)

            if (mode == AddressMode.Accumulator) {
                statusFlags.Carry = registers.A.isBitSet(7)
                registers.A = (registers.A shl 1) or (if (tempCarry) 1 else 0)

                statusFlags.setZn(registers.A)
            } else {
                var data = memory.read(address)
                statusFlags.Carry = data.isBitSet(7)

                data = (data shl 1) or (if (tempCarry) 1 else 0)
                memory.write(address, data)

                statusFlags.setZn(data)
            }
        }
    }

    private fun ror(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            val tempCarry = statusFlags.Carry
            val address = address(it)

            if (mode == AddressMode.Accumulator) {
                statusFlags.Carry = registers.A.isBitSet(0)
                registers.A = (registers.A shr 1) or (if (tempCarry) 0x80 else 0)

                statusFlags.setZn(registers.A)
            } else {
                var data = memory.read(address)
                statusFlags.Carry = data.isBitSet(0)

                data = (data shr 1) or (if (tempCarry) 0x80 else 0)
                memory.write(address, data)

                statusFlags.setZn(data)
            }
        }
    }
}


class Opcode(val op: (CPU) -> Unit)