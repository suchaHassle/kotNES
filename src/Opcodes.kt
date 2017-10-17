package kotNES

import isBitSet
import toSignedByte
import toUnsignedInt

class Opcodes {
    val opcode = Array(0xFF, { Opcode { 0 } })
    var pageCrossed: Boolean = false

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
        opcode[0x69] = adc(AddressMode.Immediate, immediate(), 2)
        opcode[0x65] = adc(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0x75] = adc(AddressMode.ZeroPageX, zeroPageXAdr(), 4)
        opcode[0x6D] = adc(AddressMode.Absolute, absolute(), 4)
        opcode[0x7D] = adc(AddressMode.AbsoluteX, absoluteX(), 4)
        opcode[0x79] = adc(AddressMode.AbsoluteY, absoluteY(), 4)
        opcode[0x61] = adc(AddressMode.IndirectX, indirectX(), 6)
        opcode[0x71] = adc(AddressMode.IndirectY, indirectY(), 5)

        /* AND Opcodes */
        opcode[0x29] = and(AddressMode.Immediate, immediate(), 2)
        opcode[0x25] = and(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0x35] = and(AddressMode.ZeroPageX, zeroPageXAdr(), 4)
        opcode[0x2D] = and(AddressMode.Absolute, absolute(), 4)
        opcode[0x3D] = and(AddressMode.AbsoluteX, absoluteX(), 4)
        opcode[0x39] = and(AddressMode.AbsoluteY, absoluteY(), 4)
        opcode[0x21] = and(AddressMode.IndirectX, indirectX(), 6)
        opcode[0x31] = and(AddressMode.IndirectY, indirectY(), 5)

        /* ASL Opcodes */
        opcode[0x0A] = asl(AddressMode.Accumulator, accumulator(), 2)
        opcode[0x06] = asl(AddressMode.ZeroPage, zeroPageAdr(), 5)
        opcode[0x16] = asl(AddressMode.ZeroPageX, zeroPageXAdr(), 6)
        opcode[0x0E] = asl(AddressMode.Absolute, absolute(), 6)
        opcode[0x1E] = asl(AddressMode.AbsoluteX, absoluteX(), 7)

        /* BCC, BCS, BEQ, BMI, BNE, BPL, BVC, BVS Opcodes */
        opcode[0x90] = bcc(AddressMode.Relative, relative(), 2)
        opcode[0xB0] = bcs(AddressMode.Relative, relative(), 2)
        opcode[0xF0] = beq(AddressMode.Relative, relative(), 2)
        opcode[0x30] = bmi(AddressMode.Relative, relative(), 2)
        opcode[0xD0] = bne(AddressMode.Relative, relative(), 2)
        opcode[0x10] = bpl(AddressMode.Relative, relative(), 2)
        opcode[0x50] = bvc(AddressMode.Relative, relative(), 2)
        opcode[0x70] = bvs(AddressMode.Relative, relative(), 2)

        /* BIT Opcodes */
        opcode[0x24] = bit(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0x2C] = bit(AddressMode.Absolute, absolute(), 4)

        /* CLC, CLD, CLI, CLV Opcode */
        opcode[0x18] = clc(AddressMode.Implied, implied(), 2)
        opcode[0xD8] = cld(AddressMode.Implied, implied(), 2)
        opcode[0x58] = cli(AddressMode.Implied, implied(), 2)
        opcode[0xB8] = clv(AddressMode.Implied, implied(), 2)

        /* CMP Opcodes */
        opcode[0xC9] = cmp(AddressMode.Immediate, immediate(), 2)
        opcode[0xC5] = cmp(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0xD5] = cmp(AddressMode.ZeroPageX, zeroPageXAdr(), 4)
        opcode[0xCD] = cmp(AddressMode.Absolute, absolute(), 4)
        opcode[0xDD] = cmp(AddressMode.AbsoluteX, absoluteX(), 4)
        opcode[0xD9] = cmp(AddressMode.AbsoluteY, absoluteY(), 4)
        opcode[0xC1] = cmp(AddressMode.IndirectX, indirectX(), 6)
        opcode[0xD1] = cmp(AddressMode.IndirectY, indirectY(), 5)

        /* CPX Opcodes */
        opcode[0xE0] = cpx(AddressMode.Immediate, immediate(), 2)
        opcode[0xE4] = cpx(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0xEC] = cpx(AddressMode.Absolute, absolute(), 4)

        /* CPY Opcodes */
        opcode[0xC0] = cpy(AddressMode.Immediate, immediate(), 2)
        opcode[0xC4] = cpy(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0xCC] = cpy(AddressMode.Absolute, absolute(), 4)

        /* DEC Opcodes */
        opcode[0xC6] = dec(AddressMode.ZeroPage, zeroPageAdr(), 5)
        opcode[0xD6] = dec(AddressMode.ZeroPageX, zeroPageXAdr(), 6)
        opcode[0xCE] = dec(AddressMode.Absolute, absolute(), 6)
        opcode[0xDE] = dec(AddressMode.AbsoluteX, absoluteX(), 7)

        /* DEX, DEY Opcodes */
        opcode[0xCA] = dex(AddressMode.Implied, implied(), 2)
        opcode[0x88] = dey(AddressMode.Implied, implied(), 2)

        /* EOR Opcodes */
        opcode[0x49] = eor(AddressMode.Immediate, immediate(), 2)
        opcode[0x45] = eor(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0x55] = eor(AddressMode.ZeroPageX, zeroPageXAdr(), 4)
        opcode[0x4D] = eor(AddressMode.Absolute, absolute(), 4)
        opcode[0x5D] = eor(AddressMode.AbsoluteX, absoluteX(), 4)
        opcode[0x59] = eor(AddressMode.AbsoluteY, absoluteY(), 4)
        opcode[0x41] = eor(AddressMode.IndirectX, indirectX(), 6)
        opcode[0x51] = eor(AddressMode.IndirectY, indirectY(), 5)

        /* INC, INX, INY Opcodes */
        opcode[0xE6] = inc(AddressMode.ZeroPage, zeroPageAdr(), 5)
        opcode[0xF6] = inc(AddressMode.ZeroPageX, zeroPageXAdr(), 6)
        opcode[0xEE] = inc(AddressMode.Absolute, absolute(), 6)
        opcode[0xFE] = inc(AddressMode.AbsoluteX, absoluteX(), 7)
        opcode[0xE8] = inx(AddressMode.Implied, implied(), 2)
        opcode[0xC8] = iny(AddressMode.Implied, implied(), 2)

        /* JMP Opcodes */
        opcode[0x4C] = jmp(AddressMode.Absolute, absolute(), 3)
        opcode[0x6C] = jmp(AddressMode.Indirect, indirect(), 5)

        /* LDA Opcodes */
        opcode[0xA9] = lda(AddressMode.Immediate, immediate(), 2)
        opcode[0xA5] = lda(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0xB5] = lda(AddressMode.ZeroPageX, zeroPageXAdr(), 4)
        opcode[0xAD] = lda(AddressMode.Absolute, absolute(), 4)
        opcode[0xBD] = lda(AddressMode.AbsoluteX, absoluteX(), 4)
        opcode[0xB9] = lda(AddressMode.AbsoluteY, absoluteY(), 4)
        opcode[0xA1] = lda(AddressMode.IndirectX, indirectX(), 6)
        opcode[0xB1] = lda(AddressMode.IndirectY, indirectY(), 5)

        /* LDX, LDY Opcodes */
        opcode[0xA2] = ldx(AddressMode.Immediate, immediate(), 2)
        opcode[0xA6] = ldx(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0xB6] = ldx(AddressMode.ZeroPageY, zeroPageYAdr(), 4)
        opcode[0xAE] = ldx(AddressMode.Absolute, absolute(), 4)
        opcode[0xBE] = ldx(AddressMode.AbsoluteY, absoluteY(), 4)
        opcode[0xA0] = ldy(AddressMode.Immediate, immediate(), 2)
        opcode[0xA4] = ldy(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0xB4] = ldy(AddressMode.ZeroPageX, zeroPageXAdr(), 4)
        opcode[0xAC] = ldy(AddressMode.Absolute, absolute(), 4)
        opcode[0xBC] = ldy(AddressMode.AbsoluteX, absoluteX(), 4)

        /* LSR Opcodes */
        opcode[0x4A] = lsr(AddressMode.Accumulator, accumulator(), 2)
        opcode[0x46] = lsr(AddressMode.ZeroPage, zeroPageAdr(), 5)
        opcode[0x56] = lsr(AddressMode.ZeroPageX, zeroPageXAdr(), 6)
        opcode[0x4E] = lsr(AddressMode.Absolute, absolute(), 6)
        opcode[0x5E] = lsr(AddressMode.AbsoluteX, absoluteX(), 7)

        /* NOP Opcode */
        opcode[0xEA] = nop(AddressMode.Implied, implied(), 2)

        /* ORA Opcodes */
        opcode[0x09] = ora(AddressMode.Immediate, immediate(), 2)
        opcode[0x05] = ora(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0x15] = ora(AddressMode.ZeroPageX, zeroPageXAdr(), 4)
        opcode[0x0D] = ora(AddressMode.Absolute, absolute(), 4)
        opcode[0x1D] = ora(AddressMode.AbsoluteX, absoluteX(), 4)
        opcode[0x19] = ora(AddressMode.AbsoluteY, absoluteY(), 4)
        opcode[0x01] = ora(AddressMode.IndirectX, indirectX(), 6)
        opcode[0x11] = ora(AddressMode.IndirectY, indirectY(), 5)

        /* ROL Opcodes */
        opcode[0x2A] = rol(AddressMode.Accumulator, accumulator(), 2)
        opcode[0x26] = rol(AddressMode.ZeroPage, zeroPageAdr(), 5)
        opcode[0x36] = rol(AddressMode.ZeroPageX, zeroPageXAdr(), 6)
        opcode[0x2E] = rol(AddressMode.Absolute, absolute(), 6)
        opcode[0x3E] = rol(AddressMode.AbsoluteX, absoluteX(), 7)

        /* ROR Opcodes */
        opcode[0x6A] = ror(AddressMode.Accumulator, accumulator(), 2)
        opcode[0x66] = ror(AddressMode.ZeroPage, zeroPageAdr(), 5)
        opcode[0x76] = ror(AddressMode.ZeroPageX, zeroPageXAdr(), 6)
        opcode[0x6E] = ror(AddressMode.Absolute, absolute(), 6)
        opcode[0x7E] = ror(AddressMode.AbsoluteX, absoluteX(), 7)

        /* SBC Opcodes */
        opcode[0xE9] = sbc(AddressMode.Immediate, immediate(), 2)
        opcode[0xE5] = sbc(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0xF5] = sbc(AddressMode.ZeroPageX, zeroPageXAdr(), 4)
        opcode[0xED] = sbc(AddressMode.Absolute, absolute(), 4)
        opcode[0xFD] = sbc(AddressMode.AbsoluteX, absoluteX(), 4)
        opcode[0xF9] = sbc(AddressMode.AbsoluteY, absoluteY(), 4)
        opcode[0xE1] = sbc(AddressMode.IndirectX, indirectX(), 6)
        opcode[0xF1] = sbc(AddressMode.IndirectY, indirectY(), 5)

        /* SEC, SED, SEI Opcodes */
        opcode[0x38] = sec(AddressMode.Implied, implied(), 2)
        opcode[0xF8] = sed(AddressMode.Implied, implied(), 2)
        opcode[0x78] = sei(AddressMode.Implied, implied(), 2)

        /* STA Opcodes */
        opcode[0x85] = sta(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0x95] = sta(AddressMode.ZeroPageX, zeroPageXAdr(), 4)
        opcode[0x8D] = sta(AddressMode.Absolute, absolute(), 4)
        opcode[0x9D] = sta(AddressMode.AbsoluteX, absoluteX(), 5)
        opcode[0x99] = sta(AddressMode.AbsoluteY, absoluteY(), 5)
        opcode[0x81] = sta(AddressMode.IndirectX, indirectX(), 6)
        opcode[0x91] = sta(AddressMode.IndirectY, indirectY(), 6)

        /* STX, STY Opcodes */
        opcode[0x86] = stx(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0x96] = stx(AddressMode.ZeroPageY, zeroPageYAdr(), 4)
        opcode[0x8E] = stx(AddressMode.Absolute, absolute(), 4)
        opcode[0x84] = sty(AddressMode.ZeroPage, zeroPageAdr(), 3)
        opcode[0x94] = sty(AddressMode.ZeroPageX, zeroPageXAdr(), 4)
        opcode[0x8C] = sty(AddressMode.Absolute, absolute(), 4)

        /* TAX, TAY, TXA, TYA Opcodes */
        opcode[0xAA] = tax(AddressMode.Implied, implied(), 2)
        opcode[0xA8] = tay(AddressMode.Implied, implied(), 2)
        opcode[0x8A] = txa(AddressMode.Implied, implied(), 2)
        opcode[0x98] = tya(AddressMode.Implied, implied(), 2)
    }

    /* Address mode memory address */

    private fun absolute(): (CPU) -> Int = { it.memory.read16(it.registers.PC + 1) }

    private fun absoluteX(): (CPU) -> Int = {
        var address = it.memory.read16(it.registers.PC + 1) + it.registers.X
        pageCrossed = isPageCrossed(address - it.registers.X, it.registers.X)
        address
    }

    private fun absoluteY(): (CPU) -> Int = {
        var address = it.memory.read16(it.registers.PC + 1) + it.registers.Y
        pageCrossed = isPageCrossed(address - it.registers.Y, it.registers.Y)
        address
    }

    private fun accumulator(): (CPU) -> Int = { 0 }

    private fun indirect(): (CPU) -> Int = { it.memory.read16wrap(it.memory.read16(it.registers.PC + 1)) }

    private fun indirectX(): (CPU) -> Int = { it.memory.read16wrap(indirectXAdr()(it)) }

    private fun indirectY(): (CPU) -> Int = {
        var address = it.memory.read16wrap(indirectYAdr()(it)) + it.registers.Y
        pageCrossed = isPageCrossed(address - it.registers.Y, it.registers.Y)
        address
    }

    private fun indirectXAdr(): (CPU) -> Int = { (it.memory.read(it.registers.PC + 1) + it.registers.X) and 0xFF }

    private fun indirectYAdr(): (CPU) -> Int = { it.memory.read(it.registers.PC + 1) }

    private fun immediate(): (CPU) -> Int = { it.registers.PC + 1 }

    private fun implied(): (CPU) -> Int = { 0 }

    private fun relative(): (CPU) -> Int = { it.registers.PC + it.memory.read(it.registers.PC + 1) + 2 }

    private fun zeroPageAdr(): (CPU) -> Int = { it.memory.read(it.registers.PC + 1) }

    private fun zeroPageXAdr(): (CPU) -> Int = { (it.memory.read(it.registers.PC + 1) + it.registers.X) and 0xFF }

    private fun zeroPageYAdr(): (CPU) -> Int = { (it.memory.read(it.registers.PC + 1) + it.registers.Y) and 0xFF }

    /* Opcode helpers */
    private fun isPageCrossed(a: Int, b: Int): Boolean {
        return a and 0xFF != b and 0xFF
    }

    private fun branch(cond: Boolean, address: Int, it: CPU, cycles: Int) {
        if (cond) {
            it.cycles += 1 + if (isPageCrossed(it.registers.PC, address)) 1 else 0
            it.registers.PC = address
        }
        it.cycles += cycles
    }

    /* Opcode methods */

    private fun adc(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            // https://stackoverflow.com/questions/29193303/6502-emulation-proper-way-to-implement-adc-and-sbc
            var mem = it.memory.read(it.registers.PC + 1)
            val carry = if (it.statusFlags.Carry) 1 else 0

            val sum = it.registers.A + mem + carry
            it.statusFlags.Carry = sum > 0xFF
            it.statusFlags.setZn(sum)

            it.statusFlags.Overflow = ((it.registers.A xor mem).inv() and (it.registers.A xor sum) and 0x80) != 0

            it.registers.A = sum
            it.cycles += cycles + if (pageCrossed) 1 else 0
        }
    }

    private fun and(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val address = address(it)
            it.registers.A = it.registers.A and it.memory.read(address)
            statusFlags.setZn(registers.A)
            it.cycles += cycles + if (pageCrossed) 1 else 0
        }
    }

    private fun asl(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            var address = address(it)
            it.cycles += cycles

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

    private fun bcc(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode { it.apply { branch(!it.statusFlags.Carry, address(it), it, cycles) } }

    private fun bcs(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode { it.apply { branch(it.statusFlags.Carry, address(it), it, cycles) } }

    private fun beq(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode { it.apply { branch(it.statusFlags.Zero, address(it), it, cycles) } }

    private fun bne(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode { it.apply { branch(!it.statusFlags.Zero, address(it), it, cycles) } }

    private fun bmi(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode { it.apply { it.registers.PC = if(it.statusFlags.Negative) address(it) else it.registers.PC } }

    private fun bpl(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode { it.apply { branch(!it.statusFlags.Negative, address(it), it, cycles) } }

    private fun bvc(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode { it.apply { branch(!it.statusFlags.Overflow, address(it), it, cycles) } }

    private fun bvs(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode { it.apply { branch(it.statusFlags.Overflow, address(it), it, cycles) } }

    private fun bit(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val address = address(it)
            var data = it.memory.read(address)
            it.cycles += cycles

            it.statusFlags.Negative = data.isBitSet(7)
            it.statusFlags.Overflow = data.isBitSet(6)
            it.statusFlags.Zero = data and it.registers.A == 0
        }
    }

    private fun clc(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.statusFlags.Carry = false
            it.cycles += cycles
        }
    }

    private fun cld(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.statusFlags.DecimalMode = false
            it.cycles += cycles
        }
    }

    private fun cli(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.statusFlags.InterruptDisable = false
            it.cycles += cycles
        }
    }

    private fun clv(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.statusFlags.Overflow = false
            it.cycles += cycles
        }
    }

    private fun cmp(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val address = address(it)
            val data = it.memory.read(address)

            it.statusFlags.Carry = it.registers.A >= data
            it.statusFlags.setZn(it.registers.A - data)
            it.cycles += cycles + if (pageCrossed) 1 else 0
        }
    }

    private fun cpx(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val address = address(it)
            val data = it.memory.read(address)

            it.statusFlags.Carry = it.registers.X >= data
            it.statusFlags.setZn(it.registers.X - data)
            it.cycles += cycles
        }
    }

    private fun cpy(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val address = address(it)
            val data = it.memory.read(address)

            it.statusFlags.Carry = it.registers.Y >= data
            it.statusFlags.setZn(it.registers.Y - data)
            it.cycles += cycles
        }
    }

    private fun dec(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val address = address(it)
            var data = it.memory.read(address)
            data--
            it.memory.write(address, data)
            it.statusFlags.setZn(data)
            it.cycles += cycles
        }
    }

    private fun dex(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.registers.X--
            it.statusFlags.setZn(it.registers.X)
            it.cycles += cycles
        }
    }

    private fun dey(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.registers.Y--
            it.statusFlags.setZn(it.registers.Y)
            it.cycles += cycles
        }
    }

    private fun eor(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val address = address(it)
            val data = it.memory.read(address)
            it.registers.A = (it.registers.A xor data) and 0xFF
            it.cycles += cycles + if (pageCrossed) 1 else 0
        }
    }

    private fun inc(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val address = address(it)
            var data = it.memory.read(address)
            data++
            it.memory.write(address, data)
            it.statusFlags.setZn(data)
            it.cycles += cycles
        }
    }

    private fun inx(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.registers.X++
            it.statusFlags.setZn(it.registers.X)
            it.cycles += cycles
        }
    }

    private fun iny(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.registers.Y++
            it.statusFlags.setZn(it.registers.Y)
            it.cycles += cycles
        }
    }

    private fun jmp(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.registers.PC = address(it)
            it.cycles += cycles
        }
    }

    private fun lda(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            var address = address(it)

            it.registers.A = it.memory.read(address)
            it.statusFlags.setZn(it.registers.A)
            it.cycles += cycles + if (pageCrossed) 1 else 0
        }
    }

    private fun ldx(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            var address = address(it)

            it.registers.X = it.memory.read(address)
            it.statusFlags.setZn(it.registers.X)
            it.cycles += cycles + if (pageCrossed) 1 else 0
        }
    }

    private fun ldy(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            var address = address(it)

            it.registers.Y = it.memory.read(address)
            it.statusFlags.setZn(it.registers.Y)
            it.cycles += cycles + if (pageCrossed) 1 else 0
        }
    }

    private fun lsr(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val address = address(it)
            it.cycles += cycles

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

    private fun nop(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        // Literally do nothing
        it.apply { it.cycles += cycles }
    }

    private fun ora(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.registers.A = it.registers.A or it.memory.read(address(it))

            it.statusFlags.setZn(it.registers.A)
            it.cycles += cycles + if (pageCrossed) 1 else 0
        }
    }

    private fun rol(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val tempCarry = statusFlags.Carry
            val address = address(it)
            it.cycles += cycles

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

    private fun ror(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val tempCarry = statusFlags.Carry
            val address = address(it)
            it.cycles += cycles

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

    private fun sbc(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            val mem = it.memory.read(address(it))
            val carry = if(it.statusFlags.Carry) 0 else 1

            val difference = it.registers.A - mem - carry
            it.statusFlags.Carry = difference >= 0
            it.statusFlags.setZn(difference)

            it.statusFlags.Overflow = ((it.registers.A xor mem) and (it.registers.A xor difference) and 0xFF) != 0

            it.registers.A = difference
            it.cycles += cycles + if (pageCrossed) 1 else 0
        }
    }

    private fun sec(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.statusFlags.Carry = true
            it.cycles += cycles
        }
    }

    private fun sed(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.statusFlags.DecimalMode = true
            it.cycles += cycles
        }
    }

    private fun sei(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.statusFlags.InterruptDisable = true
            it.cycles += cycles
        }
    }

    private fun sta(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.memory.write(address(it), it.registers.A)
            it.cycles += cycles
        }
    }

    private fun stx(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.memory.write(address(it), it.registers.X)
            it.cycles += cycles
        }
    }

    private fun sty(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.memory.write(address(it), it.registers.Y)
            it.cycles += cycles
        }
    }

    private fun tax(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.registers.X = it.registers.A
            it.cycles += cycles
            it.statusFlags.setZn(it.registers.X)
        }
    }

    private fun tay(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.registers.Y = it.registers.A
            it.statusFlags.setZn(it.registers.Y)
            it.cycles += cycles
        }
    }

    private fun txa(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.registers.A = it.registers.X
            it.statusFlags.setZn(it.registers.A)
            it.cycles += cycles
        }
    }

    private fun tya(mode: AddressMode, address: (CPU) -> Int, cycles: Int) = Opcode {
        it.apply {
            it.registers.A = it.registers.Y
            it.statusFlags.setZn(it.registers.A)
            it.cycles += cycles
        }
    }
}


class Opcode(val op: (CPU) -> Unit)