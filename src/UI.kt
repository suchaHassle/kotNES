package kotNES

import javafx.animation.AnimationTimer
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.image.PixelFormat
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import tornadofx.App
import java.lang.Long.max

private const val gameWidth = 256
private const val gameHeight = 240

class UI : FrameListener, App() {
    private lateinit var stage: Stage
    private var emulator = Emulator().also { it.addFrameListener(this) }
    private var canvas = Canvas(gameWidth.toDouble(), gameHeight.toDouble())
    private var nextFrame = ByteArray(gameWidth * gameHeight * 3)

    override fun start(stage: Stage) {
        this.stage = stage.apply {
            title = "kotNES"
            scene = Scene(StackPane().apply { children.add(canvas) })
            scene.onKeyPressed = EventHandler { event ->
                when (event.code) {
                    KeyCode.UP -> emulator.controller.setButtonState(Controller.Buttons.Up, true)
                    KeyCode.DOWN -> emulator.controller.setButtonState(Controller.Buttons.Down, true)
                    KeyCode.LEFT -> emulator.controller.setButtonState(Controller.Buttons.Left, true)
                    KeyCode.RIGHT -> emulator.controller.setButtonState(Controller.Buttons.Right, true)
                    KeyCode.Z -> emulator.controller.setButtonState(Controller.Buttons.A, true)
                    KeyCode.X -> emulator.controller.setButtonState(Controller.Buttons.B, true)
                    KeyCode.Q -> emulator.controller.setButtonState(Controller.Buttons.Start, true)
                    KeyCode.W -> emulator.controller.setButtonState(Controller.Buttons.Select, true)
                }
            }
            scene.onKeyReleased = EventHandler { event ->
                when (event.code) {
                    KeyCode.UP -> emulator.controller.setButtonState(Controller.Buttons.Up, false)
                    KeyCode.DOWN -> emulator.controller.setButtonState(Controller.Buttons.Down, false)
                    KeyCode.LEFT -> emulator.controller.setButtonState(Controller.Buttons.Left, false)
                    KeyCode.RIGHT -> emulator.controller.setButtonState(Controller.Buttons.Right, false)
                    KeyCode.Z -> emulator.controller.setButtonState(Controller.Buttons.A, false)
                    KeyCode.X -> emulator.controller.setButtonState(Controller.Buttons.B, false)
                    KeyCode.Q -> emulator.controller.setButtonState(Controller.Buttons.Start, false)
                    KeyCode.W -> emulator.controller.setButtonState(Controller.Buttons.Select, false)
                }
            }

            show()
        }

        object: AnimationTimer() {
            override fun handle(now: Long) {
                val pixelWriter = canvas.graphicsContext2D.pixelWriter
                val pixelFormat = PixelFormat.getByteRgbInstance()

                pixelWriter.setPixels(0, 0, gameWidth, gameHeight, pixelFormat, nextFrame, 0, gameWidth * 3)
            }
        }.start()

        Thread {
            with(emulator) {
                start()

                while(true) {
                    val startTime = System.currentTimeMillis()
                    stepSeconds()
                    val endTime = System.currentTimeMillis()

                    var sleepTime: Long = (((1000.0) / 60) - (endTime - startTime)).toLong()
                    sleepTime = max(sleepTime, 0)

                    Thread.sleep(sleepTime)
                }
            }
        }.start()
    }

    override fun frameUpdate(frame: IntArray) {
        emulator.evenOdd = !emulator.evenOdd

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