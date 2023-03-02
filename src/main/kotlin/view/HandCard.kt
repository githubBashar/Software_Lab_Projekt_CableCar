package view

import service.CardImageLoader
import tools.aqua.bgw.components.gamecomponentviews.CardView

/**
 *  UIComponent, Customised CardView, showing the player's hand card
 */
class HandCard(
    posX: Number, posY: Number,
    width: Number = 264, height: Number = 264,
    magnifyingGlass: MagnifyingGlass,
    viewData: ViewData
): CardSlot(
    posX = posX, posY = posY,
    width = width, height = height,
    magnifyingGlass = magnifyingGlass,
    viewData = viewData, column = -1, row = -1
) {
    //private val cardImageLoader = CardImageLoader()
    init {
        onMouseClicked = {}
        onMouseEntered = {isMouseEntered = true}
        onMouseExited = {isMouseEntered = false}
    }

    override var isMouseEntered: Boolean
        get() = super.isMouseEntered
        set(value) {
            if (isSet) {
                super.isMouseEntered = value
            }
        }

    override var cardView: CardView?
        get() = super.cardView
        set(value) {
            if (value != null) {
                super.cardView = null
            }
            super.cardView = value
        }

    /**
     *  Rotate the hand held by the player 90 degrees clockwise
     */
    fun rotateCard() {
        if (isSet) {
            val rotation = super.cardView!!.rotation
            super.cardView!!.rotation = if(rotation >= -0.5 && rotation <= 0.5) {
//                println("90")
                90.0
            }
            else if (rotation >= 89.5 && rotation <= 90.5) {
//                println("180")
                180.0
            }
            else if (rotation >= 179.5 && rotation <= 180.5) {
//                println("270")
                270.0
            }
            else {
//                println("0")
                0.0
            }
            super.rotation = super.cardView!!.rotation
        }

    }
}