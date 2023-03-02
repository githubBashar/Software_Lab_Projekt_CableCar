package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.Visual

/**
 * This class implements the appearance of the Undo/Redo button in response to mouse events
 *
 * @property setIsDisable takes over control of the isDisable property for the [TransparentButton]
 */
open class TransparentButton(
    width: Number = 200, height: Number = 200,
    posX: Number, posY: Number,
    text: String = "", font: Font = Font(),
    val imageVisual: ImageVisual
): Button(
    width = width, height = height,
    posX = posX, posY = posY,
    text = text, font = font,
) {
    final override var visual = Visual.EMPTY
        set(value) {
            super.visual = value
            field = value
        }

    init {
        visual = imageVisual
        componentStyle = "-fx-background-radius: 50px"

        onMouseEntered = {
            backgroundStyle = "-fx-background-color: rgba(0, 0, 0, 0.2);" +
                    "-fx-background-radius: 50px"
        }

        onMouseExited = {
            backgroundStyle = "-fx-background-color: rgba(0, 0, 0, 0);" +
                    "-fx-background-radius: 50px"
        }
    }

    /**
     * This property is used to set the isDisable property of this class and the associated appearance.
     * So when setting the isDisable property of this class, this property should take over the
     * isDisable property.
     */
    var setIsDisable: Boolean = false
        get() = super.isDisabled
        set(value) {
            super.isDisabled = value
            if (isDisabled) {
                this.imageVisual.transparency = 0.5
                visual = this.imageVisual
            }
            else {
                this.imageVisual.transparency = 1.0
                visual = this.imageVisual
            }
            field = value
        }
}