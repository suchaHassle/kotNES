package kotNES

import isBitSet
import toSignedByte

class Opcodes {
    val opcode = Array(0xFF, { Opcode { it.increment(-1) } })
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
        opcode[0x69] = adc(AddressMode.Immediate, 2)
        opcode[0x65] = adc(AddressMode.ZeroPage, 3)
        opcode[0x75] = adc(AddressMode.ZeroPageX, 4)
        opcode[0x6D] = adc(AddressMode.Absolute, 4)
        opcode[0x7D] = adc(AddressMode.AbsoluteX, 4)
        opcode[0x79] = adc(AddressMode.AbsoluteY, 4)
        opcode[0x61] = adc(AddressMode.IndirectX, 6)
        opcode[0x71] = adc(AddressMode.IndirectY, 5)

        /* AND Opcodes */
        opcode[0x29] = and(AddressMode.Immediate, 2)
        opcode[0x25] = and(AddressMode.ZeroPage, 3)
        opcode[0x35] = and(AddressMode.ZeroPageX, 4)
        opcode[0x2D] = and(AddressMode.Absolute, 4)
        opcode[0x3D] = and(AddressMode.AbsoluteX, 4)
        opcode[0x39] = and(AddressMode.AbsoluteY, 4)
        opcode[0x21] = and(AddressMode.IndirectX, 6)
        opcode[0x31] = and(AddressMode.IndirectY, 5)

        /* ASL Opcodes */
        opcode[0x0A] = asl(AddressMode.Accumulator, 2)
        opcode[0x06] = asl(AddressMode.ZeroPage, 5)
        opcode[0x16] = asl(AddressMode.ZeroPageX, 6)
        opcode[0x0E] = asl(AddressMode.Absolute, 6)
        opcode[0x1E] = asl(AddressMode.AbsoluteX, 7)

        /* BCC, BCS, BEQ, BMI, BNE, BPL, BVC, BVS Opcodes */
        opcode[0x90] = bcc(AddressMode.Relative, 2)
        opcode[0xB0] = bcs(AddressMode.Relative, 2)
        opcode[0xF0] = beq(AddressMode.Relative, 2)
        opcode[0x30] = bmi(AddressMode.Relative, 2)
        opcode[0xD0] = bne(AddressMode.Relative, 2)
        opcode[0x10] = bpl(AddressMode.Relative, 2)
        opcode[0x50] = bvc(AddressMode.Relative, 2)
        opcode[0x70] = bvs(AddressMode.Relative, 2)

        /* BIT Opcodes */
        opcode[0x24] = bit(AddressMode.ZeroPage, 3)
        opcode[0x2C] = bit(AddressMode.Absolute, 4)

        /* BRK Opcode */
        opcode[0x00] = brk(AddressMode.Implied, 7)

        /* CLC, CLD, CLI, CLV Opcode */
        opcode[0x18] = clc(AddressMode.Implied, 2)
        opcode[0xD8] = cld(AddressMode.Implied, 2)
        opcode[0x58] = cli(AddressMode.Implied, 2)
        opcode[0xB8] = clv(AddressMode.Implied, 2)

        /* CMP Opcodes */
        opcode[0xC9] = cmp(AddressMode.Immediate, 2)
        opcode[0xC5] = cmp(AddressMode.ZeroPage, 3)
        opcode[0xD5] = cmp(AddressMode.ZeroPageX, 4)
        opcode[0xCD] = cmp(AddressMode.Absolute, 4)
        opcode[0xDD] = cmp(AddressMode.AbsoluteX, 4)
        opcode[0xD9] = cmp(AddressMode.AbsoluteY, 4)
        opcode[0xC1] = cmp(AddressMode.IndirectX, 6)
        opcode[0xD1] = cmp(AddressMode.IndirectY, 5)

        /* CPX Opcodes */
        opcode[0xE0] = cpx(AddressMode.Immediate, 2)
        opcode[0xE4] = cpx(AddressMode.ZeroPage, 3)
        opcode[0xEC] = cpx(AddressMode.Absolute, 4)

        /* CPY Opcodes */
        opcode[0xC0] = cpy(AddressMode.Immediate, 2)
        opcode[0xC4] = cpy(AddressMode.ZeroPage, 3)
        opcode[0xCC] = cpy(AddressMode.Absolute, 4)

        /* DEC Opcodes */
        opcode[0xC6] = dec(AddressMode.ZeroPage, 5)
        opcode[0xD6] = dec(AddressMode.ZeroPageX, 6)
        opcode[0xCE] = dec(AddressMode.Absolute, 6)
        opcode[0xDE] = dec(AddressMode.AbsoluteX, 7)

        /* DEX, DEY Opcodes */
        opcode[0xCA] = dex(AddressMode.Implied, 2)
        opcode[0x88] = dey(AddressMode.Implied, 2)

        /* EOR Opcodes */
        opcode[0x49] = eor(AddressMode.Immediate, 2)
        opcode[0x45] = eor(AddressMode.ZeroPage, 3)
        opcode[0x55] = eor(AddressMode.ZeroPageX, 4)
        opcode[0x4D] = eor(AddressMode.Absolute, 4)
        opcode[0x5D] = eor(AddressMode.AbsoluteX, 4)
        opcode[0x59] = eor(AddressMode.AbsoluteY, 4)
        opcode[0x41] = eor(AddressMode.IndirectX, 6)
        opcode[0x51] = eor(AddressMode.IndirectY, 5)

        /* INC, INX, INY Opcodes */
        opcode[0xE6] = inc(AddressMode.ZeroPage, 5)
        opcode[0xF6] = inc(AddressMode.ZeroPageX, 6)
        opcode[0xEE] = inc(AddressMode.Absolute, 6)
        opcode[0xFE] = inc(AddressMode.AbsoluteX, 7)
        opcode[0xE8] = inx(AddressMode.Implied, 2)
        opcode[0xC8] = iny(AddressMode.Implied, 2)

        /* JMP, JSR Opcodes */
        opcode[0x4C] = jmp(AddressMode.Absolute, 3)
        opcode[0x6C] = jmp(AddressMode.Indirect, 5)
        opcode[0x20] = jsr(AddressMode.Absolute, 6)

        /* LDA Opcodes */
        opcode[0xA9] = lda(AddressMode.Immediate, 2)
        opcode[0xA5] = lda(AddressMode.ZeroPage, 3)
        opcode[0xB5] = lda(AddressMode.ZeroPageX, 4)
        opcode[0xAD] = lda(AddressMode.Absolute, 4)
        opcode[0xBD] = lda(AddressMode.AbsoluteX, 4)
        opcode[0xB9] = lda(AddressMode.AbsoluteY, 4)
        opcode[0xA1] = lda(AddressMode.IndirectX, 6)
        opcode[0xB1] = lda(AddressMode.IndirectY, 5)

        /* LDX, LDY Opcodes */
        opcode[0xA2] = ldx(AddressMode.Immediate, 2)
        opcode[0xA6] = ldx(AddressMode.ZeroPage, 3)
        opcode[0xB6] = ldx(AddressMode.ZeroPageY, 4)
        opcode[0xAE] = ldx(AddressMode.Absolute, 4)
        opcode[0xBE] = ldx(AddressMode.AbsoluteY, 4)
        opcode[0xA0] = ldy(AddressMode.Immediate, 2)
        opcode[0xA4] = ldy(AddressMode.ZeroPage, 3)
        opcode[0xB4] = ldy(AddressMode.ZeroPageX, 4)
        opcode[0xAC] = ldy(AddressMode.Absolute, 4)
        opcode[0xBC] = ldy(AddressMode.AbsoluteX, 4)

        /* LSR Opcodes */
        opcode[0x4A] = lsr(AddressMode.Accumulator, 2)
        opcode[0x46] = lsr(AddressMode.ZeroPage, 5)
        opcode[0x56] = lsr(AddressMode.ZeroPageX, 6)
        opcode[0x4E] = lsr(AddressMode.Absolute, 6)
        opcode[0x5E] = lsr(AddressMode.AbsoluteX, 7)

        /* NOP Opcode */
        opcode[0xEA] = nop(AddressMode.Implied, 2)

        /* ORA Opcodes */
        opcode[0x09] = ora(AddressMode.Immediate, 2)
        opcode[0x05] = ora(AddressMode.ZeroPage, 3)
        opcode[0x15] = ora(AddressMode.ZeroPageX, 4)
        opcode[0x0D] = ora(AddressMode.Absolute, 4)
        opcode[0x1D] = ora(AddressMode.AbsoluteX, 4)
        opcode[0x19] = ora(AddressMode.AbsoluteY, 4)
        opcode[0x01] = ora(AddressMode.IndirectX, 6)
        opcode[0x11] = ora(AddressMode.IndirectY, 5)

        /* PHA, PHP, PLA, PLP */
        opcode[0x48] = pha(AddressMode.Implied, 3)
        opcode[0x08] = php(AddressMode.Implied, 3)
        opcode[0x68] = pla(AddressMode.Implied, 4)
        opcode[0x28] = plp(AddressMode.Implied, 4)

        /* ROL Opcodes */
        opcode[0x2A] = rol(AddressMode.Accumulator, 2)
        opcode[0x26] = rol(AddressMode.ZeroPage, 5)
        opcode[0x36] = rol(AddressMode.ZeroPageX, 6)
        opcode[0x2E] = rol(AddressMode.Absolute, 6)
        opcode[0x3E] = rol(AddressMode.AbsoluteX, 7)

        /* ROR Opcodes */
        opcode[0x6A] = ror(AddressMode.Accumulator, 2)
        opcode[0x66] = ror(AddressMode.ZeroPage, 5)
        opcode[0x76] = ror(AddressMode.ZeroPageX, 6)
        opcode[0x6E] = ror(AddressMode.Absolute, 6)
        opcode[0x7E] = ror(AddressMode.AbsoluteX, 7)

        /* RTI, RTS Opcode */
        opcode[0x40] = rti(AddressMode.Implied, 6)
        opcode[0x60] = rts(AddressMode.Implied, 6)

        /* SBC Opcodes */
        opcode[0xE9] = sbc(AddressMode.Immediate, 2)
        opcode[0xE5] = sbc(AddressMode.ZeroPage, 3)
        opcode[0xF5] = sbc(AddressMode.ZeroPageX, 4)
        opcode[0xED] = sbc(AddressMode.Absolute, 4)
        opcode[0xFD] = sbc(AddressMode.AbsoluteX, 4)
        opcode[0xF9] = sbc(AddressMode.AbsoluteY, 4)
        opcode[0xE1] = sbc(AddressMode.IndirectX, 6)
        opcode[0xF1] = sbc(AddressMode.IndirectY, 5)

        /* SEC, SED, SEI Opcodes */
        opcode[0x38] = sec(AddressMode.Implied, 2)
        opcode[0xF8] = sed(AddressMode.Implied, 2)
        opcode[0x78] = sei(AddressMode.Implied, 2)

        /* STA Opcodes */
        opcode[0x85] = sta(AddressMode.ZeroPage, 3)
        opcode[0x95] = sta(AddressMode.ZeroPageX, 4)
        opcode[0x8D] = sta(AddressMode.Absolute, 4)
        opcode[0x9D] = sta(AddressMode.AbsoluteX, 5)
        opcode[0x99] = sta(AddressMode.AbsoluteY, 5)
        opcode[0x81] = sta(AddressMode.IndirectX, 6)
        opcode[0x91] = sta(AddressMode.IndirectY, 6)

        /* STX, STY Opcodes */
        opcode[0x86] = stx(AddressMode.ZeroPage, 3)
        opcode[0x96] = stx(AddressMode.ZeroPageY, 4)
        opcode[0x8E] = stx(AddressMode.Absolute, 4)
        opcode[0x84] = sty(AddressMode.ZeroPage, 3)
        opcode[0x94] = sty(AddressMode.ZeroPageX, 4)
        opcode[0x8C] = sty(AddressMode.Absolute, 4)

        /* TAX, TAY, TSX, TXA, TYA, TXS Opcodes */
        opcode[0xAA] = tax(AddressMode.Implied, 2)
        opcode[0xA8] = tay(AddressMode.Implied, 2)
        opcode[0xBA] = tsx(AddressMode.Implied, 2)
        opcode[0x8A] = txa(AddressMode.Implied, 2)
        opcode[0x98] = tya(AddressMode.Implied, 2)
        opcode[0x9A] = txs(AddressMode.Implied, 2)
    }

    /* Address mode memory address */

    private fun getAddress(mode: AddressMode, it: CPU): Int = when (mode) {
        AddressMode.Absolute -> it.memory.read16(it.registers.PC + 1)
        AddressMode.AbsoluteX -> {
            var address = it.memory.read16(it.registers.PC + 1) + it.registers.X
            pageCrossed = isPageCrossed(address - it.registers.X, it.registers.X)
            address and 0xFFFF
        }
        AddressMode.AbsoluteY -> {
            var address = it.memory.read16(it.registers.PC + 1) + it.registers.Y
            pageCrossed = isPageCrossed(address - it.registers.Y, it.registers.Y)
            address and 0xFFFF
        }
        AddressMode.Indirect -> it.memory.read16wrap(it.memory.read16(it.registers.PC + 1))
        AddressMode.IndirectX -> it.memory.read16wrap(indirectXAdr()(it))
        AddressMode.IndirectY -> {
            val off = indirectYAdr()(it)
            var address = it.memory.read(off) or (it.memory.read((off + 1) and 0xFF) shl 8)
            pageCrossed = address and 0xFF00 != ((address + it.registers.Y) and 0xFF00)
            (address + it.registers.Y) and 0xFFFF
            /*
            var address = it.memory.read16wrap(indirectYAdr()(it)) + it.registers.Y
            pageCrossed = isPageCrossed(address - it.registers.Y, it.registers.Y)
            address*/
        }
        AddressMode.Immediate -> it.registers.PC + 1
        AddressMode.Relative -> it.registers.PC + it.memory.read(it.registers.PC + 1).toSignedByte() + 2
        AddressMode.ZeroPage -> it.memory.read(it.registers.PC + 1)
        AddressMode.ZeroPageX -> (it.memory.read(it.registers.PC + 1) + it.registers.X) and 0xFF
        AddressMode.ZeroPageY -> (it.memory.read(it.registers.PC + 1) + it.registers.Y) and 0xFF
        else -> 0
    }

    /* Opcode helpers */
    private fun indirectXAdr(): (CPU) -> Int = { (it.memory.read(it.registers.PC + 1) + it.registers.X) and 0xFF }

    private fun indirectYAdr(): (CPU) -> Int = { it.memory.read(it.registers.PC + 1) and 0xFF }
    
    private fun isPageCrossed(a: Int, b: Int): Boolean {
        return a and 0xFF != b and 0xFF
    }

    private fun branch(cond: Boolean, address: Int, it: CPU, cycles: Int) {
        if (cond) {
            it.increment(cycles)
            it.cycles += 1 + if (isPageCrossed(it.registers.PC, address)) 1 else 0
            it.registers.PC = address
        } else {
            it.increment(cycles)
        }
    }

    private fun push(data: Int, it: CPU) {
        it.memory.write(0x100 or it.registers.S, data)
        it.registers.S--
    }

    private fun push16(data: Int, it: CPU) {
        push(data shr 8, it)
        push(data and 0xFF, it)
    }

    private fun pop(it: CPU): Int {
        it.registers.S++
        return it.memory.read(0x100 or it.registers.S)
    }

    private fun pop16(it: CPU): Int {
        return pop(it) or (pop(it) shl 8)
    }

    /* Opcode methods */

    private fun adc(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            // https://stackoverflow.com/questions/29193303/6502-emulation-proper-way-to-implement-adc-and-sbc
            var mem = it.memory.read(getAddress(mode, it))
            it.increment(cycles + if (pageCrossed) 1 else 0)
            val carry = if (it.statusFlags.Carry) 1 else 0

            val sum = it.registers.A + mem + carry
            it.statusFlags.setZn(sum)
            it.statusFlags.Carry = sum > 0xFF

            it.statusFlags.Overflow = ((it.registers.A xor mem).inv() and (it.registers.A xor sum) and 0x80) != 0

            it.registers.A = sum
        }
    }

    private fun and(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val address = getAddress(mode, it)
            it.increment(cycles + if (pageCrossed) 1 else 0)
            it.registers.A = it.registers.A and it.memory.read(address)
            statusFlags.setZn(registers.A)
        }
    }

    private fun asl(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            var address = getAddress(mode, it)
            it.increment(cycles + if (pageCrossed) 1 else 0)

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

    private fun bcc(mode: AddressMode, cycles: Int) = Opcode { it.apply { branch(!it.statusFlags.Carry, getAddress(mode, it), it, cycles) } }

    private fun bcs(mode: AddressMode, cycles: Int) = Opcode { it.apply { branch(it.statusFlags.Carry, getAddress(mode, it), it, cycles) } }

    private fun beq(mode: AddressMode, cycles: Int) = Opcode { it.apply { branch(it.statusFlags.Zero, getAddress(mode, it), it, cycles) } }

    private fun bne(mode: AddressMode, cycles: Int) = Opcode { it.apply { branch(!it.statusFlags.Zero, getAddress(mode, it), it, cycles) } }

    private fun bmi(mode: AddressMode, cycles: Int) = Opcode { it.apply {
            val address = getAddress(mode, it)
            it.increment(cycles)
            it.registers.PC = if(it.statusFlags.Negative) address else it.registers.PC
        }
    }

    private fun bpl(mode: AddressMode, cycles: Int) = Opcode { it.apply { branch(!it.statusFlags.Negative, getAddress(mode, it), it, cycles) } }

    private fun bvc(mode: AddressMode, cycles: Int) = Opcode { it.apply { branch(!it.statusFlags.Overflow, getAddress(mode, it), it, cycles) } }

    private fun bvs(mode: AddressMode, cycles: Int) = Opcode { it.apply { branch(it.statusFlags.Overflow, getAddress(mode, it), it, cycles) } }

    private fun bit(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val address = getAddress(mode, it)
            it.increment(cycles)
            var data = it.memory.read(address)

            it.statusFlags.Negative = data.isBitSet(7)
            it.statusFlags.Overflow = data.isBitSet(6)
            it.statusFlags.Zero = data and it.registers.A == 0
        }
    }

    private fun brk(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            push16(it.registers.PC, it)
            push(it.statusFlags.asByte(), it)
            it.statusFlags.InterruptDisable = true
            it.registers.PC += it.memory.read16(0xFFFE)
        }
    }

    private fun clc(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles + if (pageCrossed) 1 else 0)
            it.statusFlags.Carry = false
        }
    }

    private fun cld(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles + if (pageCrossed) 1 else 0)
            it.statusFlags.DecimalMode = false
        }
    }

    private fun cli(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles + if (pageCrossed) 1 else 0)
            it.statusFlags.InterruptDisable = false
        }
    }

    private fun clv(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles + if (pageCrossed) 1 else 0)
            it.statusFlags.Overflow = false
        }
    }

    private fun cmp(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val address = getAddress(mode, it)
            val data = it.memory.read(address)
            it.increment(cycles + if (pageCrossed) 1 else 0)

            it.statusFlags.Carry = it.registers.A >= data
            it.statusFlags.setZn(it.registers.A - data)
        }
    }

    private fun cpx(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val address = getAddress(mode, it)
            val data = it.memory.read(address)
            it.increment(cycles)

            it.statusFlags.Carry = it.registers.X >= data
            it.statusFlags.setZn(it.registers.X - data)
        }
    }

    private fun cpy(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val address = getAddress(mode, it)
            val data = it.memory.read(address)
            it.increment(cycles)

            it.statusFlags.Carry = it.registers.Y >= data
            it.statusFlags.setZn(it.registers.Y - data)
        }
    }

    private fun dec(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val address = getAddress(mode, it)
            var data = it.memory.read(address)
            it.increment(cycles)

            data--
            it.memory.write(address, data)
            it.statusFlags.setZn(data)
        }
    }

    private fun dex(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.registers.X--
            it.statusFlags.setZn(it.registers.X)
        }
    }

    private fun dey(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.registers.Y--
            it.statusFlags.setZn(it.registers.Y)
        }
    }

    private fun eor(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val address = getAddress(mode, it)
            val data = it.memory.read(address)
            it.registers.A = (it.registers.A xor data) and 0xFF
            it.statusFlags.setZn(it.registers.A)
            it.increment(cycles + if (pageCrossed) 1 else 0)
        }
    }

    private fun inc(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val address = getAddress(mode, it)
            var data = it.memory.read(address)
            it.increment(cycles)
            data++
            it.memory.write(address, data)
            it.statusFlags.setZn(data)
        }
    }

    private fun inx(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.registers.X++
            it.statusFlags.setZn(it.registers.X)
        }
    }

    private fun iny(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.registers.Y++
            it.statusFlags.setZn(it.registers.Y)
        }
    }

    private fun jmp(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val address = getAddress(mode, it)
            it.increment(cycles)
            it.registers.PC = address
        }
    }

    private fun jsr(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val address = getAddress(mode, it)
            it.increment(cycles)
            push16(it.registers.PC - 1, it)
            it.registers.PC = address
        }
    }

    private fun lda(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            var address = getAddress(mode, it)
            it.increment(cycles + if(pageCrossed) 1 else 0)
            it.registers.A = it.memory.read(address)
            it.statusFlags.setZn(it.registers.A)
        }
    }

    private fun ldx(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            var address = getAddress(mode, it)
            it.increment(cycles + if(pageCrossed) 1 else 0)

            it.registers.X = it.memory.read(address)
            it.statusFlags.setZn(it.registers.X)
        }
    }

    private fun ldy(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            var address = getAddress(mode, it)
            it.increment(cycles + if(pageCrossed) 1 else 0)

            it.registers.Y = it.memory.read(address)
            it.statusFlags.setZn(it.registers.Y)
        }
    }

    private fun lsr(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val address = getAddress(mode, it)
            it.increment(cycles)

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

    private fun nop(mode: AddressMode, cycles: Int) = Opcode {
        // Literally do nothing
        it.apply { it.increment(cycles) }
    }

    private fun ora(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.registers.A = it.registers.A or it.memory.read(getAddress(mode, it))
            it.increment(cycles + if(pageCrossed) 1 else 0)
            it.statusFlags.setZn(it.registers.A)
        }
    }

    private fun pha(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles + if(pageCrossed) 1 else 0)
            push(it.registers.A, it)
        }
    }

    private fun php(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles + if(pageCrossed) 1 else 0)
            push(it.statusFlags.asByte() or 0x10, it)
        }
    }

    private fun pla(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.registers.A = pop(it)
            it.statusFlags.setZn(it.registers.A)
        }
    }

    private fun plp(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.statusFlags.toFlags(pop(it) and 0x10.inv())
        }
    }

    private fun rol(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val tempCarry = statusFlags.Carry
            val address = getAddress(mode, it)
            it.increment(cycles + if(pageCrossed) 1 else 0)

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

    private fun ror(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val tempCarry = statusFlags.Carry
            val address = getAddress(mode, it)
            it.increment(cycles + if(pageCrossed) 1 else 0)

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

    private fun rti(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)

            it.statusFlags.toFlags(pop(it))
            it.registers.PC = pop16(it)
        }
    }

    private fun rts(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.registers.PC = pop16(it) + 1
        }
    }

    private fun sbc(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            val mem = it.memory.read(getAddress(mode, it))
            it.increment(cycles + if(pageCrossed) 1 else 0)
            val carry = if(it.statusFlags.Carry) 0 else 1

            val difference = it.registers.A - mem - carry
            it.statusFlags.setZn(difference)

            it.statusFlags.Carry = difference >= 0
            it.statusFlags.Overflow = ((it.registers.A xor mem) and (it.registers.A xor difference) and 0x80) != 0

            it.registers.A = difference
        }
    }

    private fun sec(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.statusFlags.Carry = true
        }
    }

    private fun sed(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.statusFlags.DecimalMode = true
        }
    }

    private fun sei(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles + if(pageCrossed) 1 else 0)
            it.statusFlags.InterruptDisable = true
        }
    }

    private fun sta(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.memory.write(getAddress(mode, it), it.registers.A)
            it.increment(cycles + if(pageCrossed) 1 else 0)
        }
    }

    private fun stx(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.memory.write(getAddress(mode, it), it.registers.X)
            it.increment(cycles + if(pageCrossed) 1 else 0)
        }
    }

    private fun sty(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.memory.write(getAddress(mode, it), it.registers.Y)
            it.increment(cycles + if(pageCrossed) 1 else 0)
        }
    }

    private fun tax(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.registers.X = it.registers.A
            it.increment(cycles)
            it.statusFlags.setZn(it.registers.X)
        }
    }

    private fun tay(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.registers.Y = it.registers.A
            it.statusFlags.setZn(it.registers.Y)
        }
    }

    private fun tsx(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.registers.X = it.registers.S
            it.statusFlags.setZn(it.registers.X)
        }
    }

    private fun txa(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.registers.A = it.registers.X
            it.statusFlags.setZn(it.registers.A)
        }
    }

    private fun tya(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.registers.A = it.registers.Y
            it.statusFlags.setZn(it.registers.A)
        }
    }

    private fun txs(mode: AddressMode, cycles: Int) = Opcode {
        it.apply {
            it.increment(cycles)
            it.registers.S = it.registers.X
        }
    }
}


class Opcode(val op: (CPU) -> Unit)
