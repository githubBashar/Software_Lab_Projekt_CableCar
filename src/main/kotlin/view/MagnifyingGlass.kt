package view

import service.CardImageLoader
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.visual.ImageVisual
import javax.imageio.ImageIO

/**
 * If the zoomButton is in zoom mode, when the mouse is over a [CardSlot]
 * where cards are already placed, the cards currently placed on the [CardSlot]
 * will be displayed.
 */
class MagnifyingGlass(
    posX: Number, posY: Number,
    publisher: ZoomButton
): CardView(
    posX = posX, posY = posY,
    width = 528, height = 528,
    front = ImageVisual(image = ImageIO.read(CardImageLoader::class.java.getResource("/magnifyingglass.png")))
) {
    var cardView: CardView? = null
        set(value) {
            field = value
            updateStatus()
        }

    /**
     * This property will set by the publisher if necessary.
     * This property SHOULD NOT BE CHANGED in other cases.
     */
    var canBeZoomed = false
        set(value) {
            field = value
            updateStatus()
        }

    /**
     * Update the current [CardSlot] appearance status
     */
    private fun updateStatus() {
        if (cardView == null || !canBeZoomed) {
            frontVisual = ImageVisual(
                image = ImageIO.read(CardImageLoader::class.java.getResource("/magnifyingglass.png"))
            )
            this.rotation = 0.0
        }
        else {
            frontVisual = cardView!!.frontVisual
            this.rotation = cardView!!.rotation
        }
    }

    init {
        showFront()
        publisher.register(this)
    }

}