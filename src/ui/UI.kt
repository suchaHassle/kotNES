package kotNES.ui

import kotNES.Controller
import kotNES.Emulator
import javax.swing.*
import javax.swing.filechooser.FileFilter
import java.awt.event.*
import java.awt.*
import java.io.File
import java.util.concurrent.Semaphore
import javax.imageio.ImageIO
import javax.swing.ButtonGroup
import javax.swing.JMenu
import javax.swing.JPopupMenu
import javax.swing.SwingUtilities
import javax.swing.JSeparator

private const val gameWidth = 256
private const val gameHeight = 240

class UI {
    fun start() {
        // Use system-default look and feel.
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        // Disable lightweight popup because our panel is heavy weight.
        JPopupMenu.setDefaultLightWeightPopupEnabled(false)

        // Disable OpenGL and enable Direct3D is the only way Direct3D renderer can work.
        // The OpenGL renderer doesn't seem to care if it is disabled. It works just fine.
        System.setProperty("sun.java2d.opengl", "false")
        System.setProperty("sun.java2d.d3d", "true")

        var rom: File? = selectROM()
        var emulator: Emulator

        if (rom != null) {
            emulator = Emulator(rom!!.toPath())
            initUI(emulator)
        }
    }

    fun selectROM(): File? {
        var selectedFile: File? = null

        // We use this Semaphore to wait until the user selects a ROM.
        val selectLock = Semaphore(1)

        // Create the frame.
        val dialog = object : JFrame("kotNES") {
            init {
                // Layout of the Frame.
                title = "kotNES"
                size = Dimension(gameWidth * 2, gameHeight * 2)
                layout = BorderLayout()

                // Stop the waiting if the window is closed.
                // #action stop waiting on window close
                addWindowListener(object : WindowAdapter() {
                    override fun windowClosed(e: WindowEvent) {
                        selectLock.release()
                    }
                })
            }
        }
        selectLock.acquireUninterruptibly()

        val acceptor = object : FileFilter() {
            /**
             * {@inheritDoc}
             */
            override fun getDescription(): String {
                return "NES ROM (*.nes, *.NES, *.rom)"
            }

            /**
             * {@inheritDoc}
             */
            override fun accept(f: File): Boolean {
                if (f.isDirectory) return true
                val name = f.name
                return name.endsWith(".nes") || name.endsWith(".NES") || name.endsWith(".rom")
            }
        }

        dialog.jMenuBar = object : JMenuBar() { init {
            add(object : JMenu("File") { init {
                add(object : JMenuItem("Open") { init {
                    addActionListener {
                        var chooser = JFileChooser()
                        chooser.dialogTitle = "Choose a game..."
                        chooser.isVisible = true
                        chooser.fileFilter = acceptor
                        chooser.currentDirectory = File(".")
                        chooser.dialogType = JFileChooser.OPEN_DIALOG

                        if (chooser.showDialog(dialog, "Load") == JFileChooser.APPROVE_OPTION) {
                            selectedFile = chooser.selectedFile
                            selectLock.release()
                            dialog.dispose()
                        }
                    }
                }})
            }})
        }}
        dialog.isVisible = true
        dialog.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        selectLock.acquireUninterruptibly()

        return selectedFile
    }

    fun initUI(emulator: Emulator) {
        var display = HeavyDisplayPanel(emulator)
        emulator.display = display
        var disp = JFrame("kotNES - ${emulator.cartridge.filePath.fileName}")

        display.addKeyListener(object : KeyAdapter() {
            private fun toggle(e: KeyEvent, to: Boolean) {
                when (e.keyCode) {
                    KeyEvent.VK_UP -> emulator.controller.setButtonState(Controller.Buttons.Up, to)
                    KeyEvent.VK_DOWN -> emulator.controller.setButtonState(Controller.Buttons.Down, to)
                    KeyEvent.VK_LEFT -> emulator.controller.setButtonState(Controller.Buttons.Left, to)
                    KeyEvent.VK_RIGHT -> emulator.controller.setButtonState(Controller.Buttons.Right, to)
                    KeyEvent.VK_X -> emulator.controller.setButtonState(Controller.Buttons.A, to)
                    KeyEvent.VK_Z -> emulator.controller.setButtonState(Controller.Buttons.B, to)
                    KeyEvent.VK_ENTER -> emulator.controller.setButtonState(Controller.Buttons.Start, to)
                    KeyEvent.VK_S -> emulator.controller.setButtonState(Controller.Buttons.Select, to)
                }
            }

            override fun keyPressed(e: KeyEvent) {
                toggle(e, true)
            }

            override fun keyReleased(e: KeyEvent) {
                toggle(e, false)
            }
        })

        display.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent?) {
                // Only show meny on right click.
                if (!SwingUtilities.isRightMouseButton(e))
                    return

                // Create the menu.
                val menu = JPopupMenu()

                // Add renderer switching.
                menu.add(object : JMenu("Renderer") {
                    init {
                        // A button group for our radio buttons.
                        val group = ButtonGroup()

                        // Loop over renderers.
                        for (renderer in emulator.ppu.renderers) {
                            // Get the menu radio item for each renderer.
                            val menuItem = renderer.getRadioMenuItem(emulator.ppu)

                            // Add the radio button to the group so they are exclusive.
                            group.add(menuItem)

                            // Make the current renderer selected.
                            if (renderer === emulator.ppu.currentRenderer)
                                group.setSelected(menuItem.model, true)

                            add(menuItem)
                        }
                    }
                })

                menu.add(JSeparator())

                menu.add(object : JMenuItem("Save screen as image", KeyEvent.VK_P) {
                    init {
                        addActionListener {
                            var iterations = 0
                            var screenshot = File("Screenshot.png")
                            while (screenshot.exists()) {
                                screenshot = File("Screenshot ($iterations).png")
                            }

                            ImageIO.write(emulator.ppu.screenBuffer, "png", screenshot)
                        }
                    }
                })

                menu.add(object : JMenuItem("Save screen to clipboard", KeyEvent.VK_PRINTSCREEN) {
                    init {
                        var clipboard = CopyImageToClipboard()

                        addActionListener {
                            clipboard.copyToClipboard(emulator.ppu.screenBuffer)
                        }
                    }
                })

                menu.show(e?.component, e!!.x, e.y)
            }
        })

        SwingUtilities.invokeLater {
            disp.contentPane = display
            disp.pack()
            disp.isResizable = false
            disp.setLocationRelativeTo(null)
            disp.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            disp.isVisible = true
            display.requestFocus()
            emulator.ppu.initRenderer()
            if (emulator.codeExecutionThread.state == Thread.State.NEW)
                emulator.codeExecutionThread.start()
        }

        // Print debug information.
        System.err.println(emulator.cartridge.filePath.fileName.toString())
        System.err.println(emulator.cartridge)
        System.out.flush()
    }
}