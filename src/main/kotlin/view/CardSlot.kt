package view

import service.CardImageLoader
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import javax.imageio.ImageIO

/**
 * This class defines a single slot where [CardView] can be placed
 */
open class CardSlot(
    posX: Number, posY: Number,
    width: Number = 264, height: Number = 264,
    /*val publisher: ZoomButton,*/ val magnifyingGlass: MagnifyingGlass,
    val viewData: ViewData, val column: Int,
    val row: Int
): CardView(
    posX = posX, posY = posY,
    width = width, height = height,
    front = ColorVisual.TRANSPARENT
) {

    private var isSetProperty = false

    /**
     * This property describes whether this [CardSlot] has been placed with a [CardView]
     */
    val isSet: Boolean
        get() {
            return isSetProperty
        }

    /**
     * This property holds the [CardView] placed on that [CardSlot], or null when no [CardView]
     * is placed on the [CardSlot].
     * - In addition, when a [CardView] is placed in the [CardSlot], the [frontVisual] of the
     * [CardSlot] will display the [frontVisual] of the [CardView] placed in that slot;
     * otherwise, the [frontVisual] of the card slot will be set to [ColorVisual.TRANSPARENT]
     */
    open var cardView: CardView? = null
        set(value) {
            printDiagnostics(value)
            if (value == field) return

            if (value == null) {
                isSetProperty = false
                frontVisual = ColorVisual.TRANSPARENT
            }
            else {
                check(field == null) {"This CardSlot (Column = $column, Row = $row) has already been placed."}
                isSetProperty = true
                frontVisual = value.frontVisual
                this.rotation = value.rotation
            }
            field = value
        }

    open var isMouseEntered = false
        set(value) {
            field = value
            updateStatus()
        }
    init {
        onMouseClicked = {
            if (!isSetProperty) {
                viewData.selectedCardSlot = this
            }
        }
        showFront()
//        publisher.register(this)

        onMouseEntered = {
            isMouseEntered = true
        }
        onMouseExited = {
            isMouseEntered = false
        }

    }

    /**
     * This method will respond to mouse events. There are generally two scenarios:
     * 1. The card has been placed
     * 2. card has not been placed
     *
     * - For the first case, if the [ZoomButton] is in zoom out status & the mouse enter the
     * object (card), the length and width of the card will be doubled to make it easier for
     * players to see the details of the card. If one of the above conditions is not met then
     * the card reverts to its original size
     *
     * - For the second case, only mouse events will be noticed. When the mouse enters the object,
     * a shadow will be attached to the object. The shadow will disappear when the mouse leaves
     * the object
     */
    private fun updateStatus() {
//        println("isMouseEntered: $isMouseEntered, canBeZoomed: $canBeZoomed")
        // if the card have already set, the card can be zoomed in
        /*if (isSetProperty) {
            // zoom in
            magnifyingGlass.cardView = if ( canBeZoomed && isMouseEntered) {
                cardView
            }
            // zoom out
            else {
                null
            }
        }*/
        // If the card has not been set, a shadow can be attached to the empty area
        if (!isSetProperty) {
            if (isMouseEntered) {
                super.frontVisual = if(this == viewData.selectedCardSlot) {
                    CompoundVisual(
                        ColorVisual(0, 0, 0, 25),
                        ImageVisual(ImageIO.read(CardImageLoader
                        ::class.java.getResource("/targetVisual.png")))
                    )
                }
                else {
                    ColorVisual(0, 0, 0, 25)
                }
            }
            else {
                super.frontVisual = if (this == viewData.selectedCardSlot) {
                    ImageVisual(ImageIO.read(CardImageLoader
                    ::class.java.getResource("/targetVisual.png")))
                }
                else {
                    ColorVisual.TRANSPARENT
                }
            }
        }
        else {
            if (isMouseEntered) {
                magnifyingGlass.cardView = this.cardView
            }
            else {
                magnifyingGlass.cardView = null
            }

        }
    }

    /**
     * print diagnostics information
     */
    private fun printDiagnostics(value: CardView?) {
        if (row == -1) return

        if (cardView != null) {
            if (value != null) {
                if (value != cardView) {
                    findID(cardView!!)
                    findID(value)
                }
            }
        }
        else {
            if (value != null) { findID(value)}
        }
    }

    private fun findID(target: CardView):Int {
        for (i in viewData.allCards.indices) {
            if (target == viewData.allCards[i]) { return i }
        }
        return -1
    }

}