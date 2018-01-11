package kotNES.mapper

import kotNES.CPU
import kotNES.Emulator
import kotNES.Mapper

class MMC3(emulator: Emulator) : Mapper(emulator) {
    private var bank: Int = 0
    private var irqCounter: Int = 0
    private var irqCounterReload: Int = 0
    private var irqEnabled: Boolean = false
    private var prgRomMode: Boolean = false
    private var chrRomMode: Boolean = false
    private var prgRamEnable: Boolean = false
    private var bankRegisters = IntArray(8)
    private var chrBankOffsets = IntArray(8)
    private var prgBankOffsets: IntArray = intArrayOf(0, 0x2000, lastBankOffset, lastBankOffset + 0x2000)

    override fun read(address: Int): Int = when {
        address in 0x0000..0x1FFF -> emulator.cartridge.readCHRRom(chrBankOffsets[address / 0x400] + (address % 0x400))
        address in 0x6000..0x7FFF -> emulator.cartridge.readPRGRam(address - 0x6000)
        address in 0x8000..0xFFFF -> emulator.cartridge.readPRGRom(prgBankOffsets[(address - 0x8000) / 0x2000] + (address % 0x2000))
        else -> throw IndexOutOfBoundsException("$address is out of bounds")
    }

    override fun write(address: Int, value: Int) {
        val even = address and 0x01 == 0

        when (address) {
            in 0x0000..0x2000 -> emulator.cartridge.writeCHRRom(chrBankOffsets[address / 0x400] + (address % 0x400), value)
            in 0x6000..0x7FFF -> if (prgRamEnable) emulator.cartridge.writePRGRam(address - 0x6000, value)
            in 0x8000..0x9FFF -> if (even) writeBankSelect(value) else writeBankData(value)
            in 0xA000..0xBFFF -> if (even) writeMirror(value) else prgRamEnable = value and 0xC0 == 0x80
            in 0xC000..0xDFFF -> if (even) irqCounterReload = value and 0xFF else irqCounter = 0
            in 0xE000..0xFFFF -> irqEnabled = !even
        }
    }

    private fun writeBankSelect(value: Int) {
        bank = value and 7
        prgRomMode = ((value shr 6) and 0x01) != 0
        chrRomMode = ((value shr 7) and 0x01) != 0
        updateBankOffsets()
    }

    private fun writeBankData(value: Int) {
        bankRegisters[bank] = value and 0xFF
        updateBankOffsets()
    }

    private fun writeMirror(value: Int) {
        when (value and 1) {
            0 -> mirroringType = VramMirroring.Vertical
            1 -> mirroringType = VramMirroring.Horizontal
        }
    }

    private fun clock() {
        if (irqCounter == 0) irqCounter = irqCounterReload
        else {
            irqCounter--
            if (irqCounter == 0 && irqEnabled) emulator.cpu.triggerInterrupt(CPU.Interrupts.IRQ)
        }
    }

    override fun step() {
        val scanline = emulator.ppu.scanline
        val cycle = emulator.ppu.cycle

        if (emulator.ppu.renderingEnabled && cycle == 315 && scanline in 0..239) clock()
    }

    private fun updateBankOffsets() {
        if (chrRomMode) {
            chrBankOffsets[0] = bankRegisters[2]
            chrBankOffsets[1] = bankRegisters[3]
            chrBankOffsets[2] = bankRegisters[4]
            chrBankOffsets[3] = bankRegisters[5]
            chrBankOffsets[4] = (bankRegisters[0] and 0xFE)
            chrBankOffsets[5] = (bankRegisters[0] or 0x01)
            chrBankOffsets[6] = (bankRegisters[1] and 0xFE)
            chrBankOffsets[7] = (bankRegisters[1] or 0x01)
        } else {
            chrBankOffsets[0] = (bankRegisters[0] and 0xFE)
            chrBankOffsets[1] = (bankRegisters[0] or 0x01)
            chrBankOffsets[2] = (bankRegisters[1] and 0xFE)
            chrBankOffsets[3] = (bankRegisters[1] or 0x01)
            chrBankOffsets[4] = bankRegisters[2]
            chrBankOffsets[5] = bankRegisters[3]
            chrBankOffsets[6] = bankRegisters[4]
            chrBankOffsets[7] = bankRegisters[5]
        }

        if (prgRomMode) {
            prgBankOffsets[0] = lastBankOffset
            prgBankOffsets[1] = bankRegisters[7] * 0x2000
            prgBankOffsets[2] = bankRegisters[6] * 0x2000
            prgBankOffsets[3] = lastBankOffset + 0x2000
        } else {
            prgBankOffsets[0] = bankRegisters[6] * 0x2000
            prgBankOffsets[1] = bankRegisters[7] * 0x2000
            prgBankOffsets[2] = lastBankOffset
            prgBankOffsets[3] = lastBankOffset + 0x2000
        }

        for (i in 0..(prgBankOffsets.size - 1))
            prgBankOffsets[i] %= emulator.cartridge.prgROM.size

        for (i in 0..(chrBankOffsets.size - 1))
            chrBankOffsets[i] = (chrBankOffsets[i] * 0x400 % emulator.cartridge.chrROM.size)
    }
}