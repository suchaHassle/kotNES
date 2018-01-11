package kotNES

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Panel

class HeavyDisplayPanel(private var emulator: Emulator) : Panel() {
    init {
        background = Color.BLACK

        val sz = Dimension(emulator.ppu.gameWidth, emulator.ppu.gameHeight)
        maximumSize = sz
        minimumSize = sz
        size = sz
        preferredSize = sz

        ignoreRepaint = true
    }

    override fun addNotify() {
        super.addNotify()

        emulator.display = this
    }

    override fun paint(g: Graphics?) {
        throw RuntimeException()
    }
}