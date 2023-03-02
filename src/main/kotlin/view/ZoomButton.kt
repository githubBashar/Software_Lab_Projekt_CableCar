package view

import service.CardImageLoader
import tools.aqua.bgw.event.KeyCode
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import javax.imageio.ImageIO

/**
 * - This class implement the zoom in/out button, click the button can activate the zoom
 * function and click again can de-activate it.
 * - The second method of activation is by pressing the ctrl key, and releasing the change
 * key will automatically de-activate
 * - During the initialization phase of the game, each [CardSlot] that needs the zoom-function
 * needs to call the [register] method of this class in order to be able to listen to the
 * CardSlot.canBeZoomed state later.
 * - When the value of [isPressedProperty] changes, the information is automatically broadcast to
 * all registered [CardSlot]
 */
class ZoomButton(
    posX: Number, posY: Number,
    text: String = "", font: Font = Font(),
): TransparentButton(
    posX = posX, posY = posY,
    text = text, font = font,
    imageVisual = ImageVisual(ImageIO.read(
        CardImageLoader::class.java.getResource("/minus.magnifyingglass.png")))
) {
    private val subscriber = mutableListOf<MagnifyingGlass>()

    /**
     *  register the [MagnifyingGlass], to enable the [MagnifyingGlass] to respond to keyboard events.
     */
    fun register(magnifyingGlass: MagnifyingGlass) {
        subscriber.add(magnifyingGlass)
    }

    private var isPressedProperty = false
        set(value) {
            field = value
            if (value) {
                super.visual = zoomInVisual
            }
            else {
                super.visual = zoomOutVisual
            }
            subscriber.forEach {
                it.canBeZoomed = value
            }
        }

    // The visual for the status zoom in
    private val zoomInVisual = ImageVisual(ImageIO.read(
        CardImageLoader::class.java.getResource("/plus.magnifyingglass.png")))

    // The visual for the status zoom out
    private val zoomOutVisual = ImageVisual(ImageIO.read(
        CardImageLoader::class.java.getResource("/minus.magnifyingglass.png")))

    init {
        onMouseClicked = {
            isPressedProperty = !isPressedProperty
        }

        onKeyPressed = {
            if (it.keyCode == KeyCode.CONTROL){
                isPressedProperty = true
            }
        }

        onKeyReleased = {
            if (it.keyCode == KeyCode.CONTROL) {
                isPressedProperty = false
            }
        }
    }
}