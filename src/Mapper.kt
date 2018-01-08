package kotNES

open abstract class Mapper(protected var emulator: Emulator) {
    enum class VramMirroring {
        Horizontal, Vertical, SingleLower, SingleUpper
    }

    protected var lastBankOffset = emulator.cartridge.prgROM.size - 0x4000

    protected lateinit var mirroringType: VramMirroring

    abstract fun read(address: Int): Int

    abstract fun write(address: Int, value: Int)

    open fun step() {

    }
}