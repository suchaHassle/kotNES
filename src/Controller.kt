package kotNES

import asInt

class Controller {
    enum class Buttons(val value: Int) {
        A(0), B(1), Select(2), Start(3), Up(4), Down(5), Left(6), Right(7)
    }
    private var strobe: Boolean = false
    private var buttonState = BooleanArray(8)
    private var currButtonIndex: Int = 0

    fun setButtonState(button: Buttons, state: Boolean) {
        buttonState[button.value] = state
    }

    fun writeControllerInput(input: Int) {
        strobe = (input and 1) == 1
        if (strobe) currButtonIndex = 0
    }

    fun readControllerOutput(): Int {
        if (currButtonIndex > 7) return 1

        val state = buttonState[currButtonIndex]
        if (!strobe) currButtonIndex++

        return state.asInt()
    }
}