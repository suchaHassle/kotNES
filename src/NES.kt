package kotNES

fun main(args: Array<String>) {
    var emulator = Emulator()
    emulator.start()
    while (true) {
        emulator.stepSeconds(1.0)
    }
}