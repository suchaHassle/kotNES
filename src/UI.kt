package kotNES

import javax.swing.*

class UI {
    init {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        // Disable lightweight popup because our panel is heavy weight.
        JPopupMenu.setDefaultLightWeightPopupEnabled(false)

        // Disable OpenGL and enable Direct3D is the only way Direct3D renderer can work.
        // The OpenGL renderer doesn't seem to care if it is disabled. It works just fine.
        System.setProperty("sun.java2d.opengl", "false")
        System.setProperty("sun.java2d.d3d", "true")
    }
}