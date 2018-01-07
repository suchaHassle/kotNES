package kotNES

import javafx.animation.AnimationTimer
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.image.PixelFormat
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import tornadofx.App
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.concurrent.thread

private const val gameWidth = 256
private const val gameHeight = 240

class UI : FrameListener, App() {
    private lateinit var stage: Stage
    private var emulator = Emulator().also { it.addFrameListener(this) }
    private var canvas = Canvas(gameWidth.toDouble(), gameHeight.toDouble())
    private var nextFrame = ByteArray(gameWidth * gameHeight * 3)
    private var last60 = 0

    override fun start(stage: Stage) {
        this.stage = stage.apply {
            title = "kotNES"
            scene = Scene(StackPane().apply { children.add(canvas) })
            show()
        }

        object: AnimationTimer() {
            override fun handle(now: Long) {
                val pixelWriter = canvas.graphicsContext2D.pixelWriter
                val pixelFormat = PixelFormat.getByteRgbInstance()

                pixelWriter.setPixels(0, 0, gameWidth, gameHeight, pixelFormat, nextFrame, 0, gameWidth * 3)
            }
        }.start()

        thread {
            with(emulator) {
                start()

                while(true) {
                    stepSeconds(1.0)
                }
            }
        }
    }

    override fun frameUpdate(frame: IntArray) {
        if (last60++ == 120) {
            var img = BufferedImage(gameWidth, gameHeight, BufferedImage.TYPE_INT_RGB)

            var q = 0

            for (y in 0..(gameHeight - 1))
                for (x in 0..(gameWidth - 1)) {
                    img.setRGB(x, y, frame[q++])
                }

            ImageIO.write(img, "jpg", File("frame.jpg"))

            println("saved bitmap")
        }

        var i = 0

        for (pixel in frame) {
            val r = (pixel shr 16).toByte()
            val g = (pixel shr 8).toByte()
            val b = pixel.toByte()

            nextFrame[i] = r
            nextFrame[i+1] = g
            nextFrame[i+2] = b

            i += 3
        }
        //nextFrame = frame
    }
}

interface FrameListener {
    fun frameUpdate(frame: IntArray)
}