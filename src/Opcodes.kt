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
        /* ADC Opcodes */
        opcode[0x69] = adc(AddressMode.Immediate, immediate())
        opcode[0x65] = adc(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x75] = adc(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0x6D] = adc(AddressMode.Absolute, absolute())
        opcode[0x7D] = adc(AddressMode.AbsoluteX, absoluteX())
        opcode[0x79] = adc(AddressMode.AbsoluteY, absoluteY())
        opcode[0x61] = adc(AddressMode.IndirectX, indirectX())
        opcode[0x71] = adc(AddressMode.IndirectY, indirectY())

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

        /* LDX, LDY Opcodes */
        opcode[0xA2] = ldx(AddressMode.Immediate, immediate())
        opcode[0xA6] = ldx(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0xB6] = ldx(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0xAE] = ldx(AddressMode.Absolute, absolute())
        opcode[0xBE] = ldx(AddressMode.AbsoluteX, absoluteX())
        opcode[0xA0] = ldy(AddressMode.Immediate, immediate())
        opcode[0xA4] = ldy(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0xB4] = ldy(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0xAC] = ldy(AddressMode.Absolute, absolute())
        opcode[0xBC] = ldy(AddressMode.AbsoluteX, absoluteX())

        /* LSR Opcodes */
        opcode[0x4A] = lsr(AddressMode.Accumulator, accumulator())
        opcode[0x46] = lsr(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x56] = lsr(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0x4E] = lsr(AddressMode.Absolute, absolute())
        opcode[0x5E] = lsr(AddressMode.AbsoluteX, absoluteX())

        /* NOP Opcode */
        opcode[0xEA] = nop(AddressMode.Implied, implied())

        /* ORA Opcodes */
        opcode[0x09] = ora(AddressMode.Immediate, immediate())
        opcode[0x05] = ora(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x15] = ora(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0x0D] = ora(AddressMode.Absolute, absolute())
        opcode[0x1D] = ora(AddressMode.AbsoluteX, absoluteX())
        opcode[0x19] = ora(AddressMode.AbsoluteY, absoluteY())
        opcode[0x01] = ora(AddressMode.IndirectX, indirectX())
        opcode[0x11] = ora(AddressMode.IndirectY, indirectY())

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

        /* SEC, SED, SEI Opcodes */
        opcode[0x38] = sec(AddressMode.Implied, implied())
        opcode[0xF8] = sed(AddressMode.Implied, implied())
        opcode[0x78] = sei(AddressMode.Implied, implied())

        /* STA Opcodes */
        opcode[0x85] = sta(AddressMode.Immediate, immediate())
        opcode[0x95] = sta(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x8D] = sta(AddressMode.ZeroPageX, zeroPageXAdr())
        opcode[0x9D] = sta(AddressMode.Absolute, absolute())
        opcode[0x99] = sta(AddressMode.AbsoluteX, absoluteX())
        opcode[0x81] = sta(AddressMode.AbsoluteY, absoluteY())
        opcode[0x91] = sta(AddressMode.IndirectX, indirectX())

        /* STX, STY Opcodes */
        opcode[0x86] = stx(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x96] = stx(AddressMode.ZeroPageY, zeroPageYAdr())
        opcode[0x8E] = stx(AddressMode.Absolute, absolute())
        opcode[0x84] = sty(AddressMode.ZeroPage, zeroPageAdr())
        opcode[0x94] = sty(AddressMode.ZeroPageY, zeroPageAdr())
        opcode[0x8C] = sty(AddressMode.Absolute, absolute())

        /* TAX, TAY, TXA, TYA Opcodes */
        opcode[0xAA] = tax(AddressMode.Implied, implied())
        opcode[0xA8] = tay(AddressMode.Implied, implied())
        opcode[0x8A] = txa(AddressMode.Implied, implied())
        opcode[0x98] = tya(AddressMode.Implied, implied())
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

    private fun adc(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            // https://stackoverflow.com/questions/29193303/6502-emulation-proper-way-to-implement-adc-and-sbc
            var mem = it.memory.read(it.registers.PC + 1)
            val carry = if (it.statusFlags.Carry) 1 else 0

            val sum = it.registers.A + mem + carry
            it.statusFlags.Carry = sum > 0xFF
            it.statusFlags.setZn(sum)

            it.statusFlags.Overflow = ((it.registers.A xor mem).inv() and (it.registers.A xor sum) and 0x80) != 0

            it.registers.A = sum
        }
    }

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

    private fun ldx(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            var address = address(it)

            it.registers.X = it.memory.read(address)
            it.statusFlags.setZn(it.registers.X)
        }
    }

    private fun ldy(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            var address = address(it)

            it.registers.Y = it.memory.read(address)
            it.statusFlags.setZn(it.registers.Y)
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

    private fun nop(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        // Literally do nothing
    }

    private fun ora(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.registers.A = it.registers.A or it.memory.read(address(it))

            it.statusFlags.setZn(it.registers.A)
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

    private fun sec(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.statusFlags.Carry = true
        }
    }

    private fun sed(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.statusFlags.DecimalMode = true
        }
    }

    private fun sei(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.statusFlags.InterruptDisable = true
        }
    }

    private fun sta(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.memory.write(address(it), it.registers.A)
        }
    }

    private fun stx(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.memory.write(address(it), it.registers.X)
        }
    }

    private fun sty(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.memory.write(address(it), it.registers.Y)
        }
    }

    private fun tax(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.registers.X = it.registers.A

            it.statusFlags.setZn(it.registers.X)
        }
    }

    private fun tay(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.registers.Y = it.registers.A

            it.statusFlags.setZn(it.registers.Y)
        }
    }

    private fun txa(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.registers.A = it.registers.X

            it.statusFlags.setZn(it.registers.A)
        }
    }

    private fun tya(mode: AddressMode, address: (CPU) -> Int) = Opcode {
        it.apply {
            it.registers.A = it.registers.Y

            it.statusFlags.setZn(it.registers.A)
        }
    }
}


class Opcode(val op: (CPU) -> Unit)