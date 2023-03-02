package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * Added animation effects when the mouse hovers over it, spruced up the native buttons.
 *
 * @see [Button]
 */
class GameButton(
    width: Number, height: Number,
    posX: Number, posY: Number,
    text: String = "", font: Font = Font(),
    val backgroundColor:ColorVisual
):Button(
    posX = posX, posY = posY,
    width = width, height = height,
    text = text, font = font,
    visual = ColorVisual.TRANSPARENT
) {

    private val mouseOverColor:ColorVisual = ColorVisual(
        backgroundColor.copy().color.darker()
    )

    private val shortestSide = minOf(width.toDouble(), height.toDouble())

    private val backgroundStyleNormal = "-fx-background-color: rgba(${colorVisualToString(backgroundColor)});" +
            "-fx-background-radius: ${shortestSide / 4}px;"

    private val backgroundStyleOnMouseOver = "-fx-background-color: rgba(${colorVisualToString(mouseOverColor)});" +
            "-fx-background-radius: ${shortestSide / 4}px;"

    init {
        componentStyle = "-fx-background-radius: ${shortestSide / 4}px"
        backgroundStyle = backgroundStyleNormal

        onMouseEntered = {
            backgroundStyle = backgroundStyleOnMouseOver
        }

        onMouseExited = {
            backgroundStyle = backgroundStyleNormal
        }
    }

    private fun colorVisualToString(colorVisual: ColorVisual):String{
        val red = colorVisual.color.red.toString()
        val green = colorVisual.color.green.toString()
        val blue = colorVisual.color.blue.toString()
        val alpha = colorVisual.color.alpha.toString()
        return "$red, $green, $blue, $alpha"
    }
}