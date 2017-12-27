package kotNES

class PPU(private var emulator: Emulator) {
    var ppuMemory = PpuMemory(emulator)
}