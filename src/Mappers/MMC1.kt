package kotNES.mapper

import kotNES.Emulator
import kotNES.Mapper

class MMC1(emulator: Emulator) : Mapper(emulator) {
    override fun read(address: Int): Int = when(address) {
        0 -> 0
        else -> 0
    }

    override fun write(address: Int, value: Int) {

    }
}