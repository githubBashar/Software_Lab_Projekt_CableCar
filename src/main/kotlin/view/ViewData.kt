package view

import service.CardImageLoader
import entity.Card
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import javax.imageio.ImageIO

/**
 * This class contains some necessary view layer game data.
 *
 * @property allCards is the [CardView] corresponding to each card.
 * @property playerHandCard is the [CardView] corresponding to each player's hand card.
 */
class ViewData(private val placeCardButton : GameButton) {
    private val cardImageLoader = CardImageLoader()

    /**
     * It holds the [CardView] of all the [Card]s, the index of the element [CardView]
     * is the ID of this card. E.g. allCard[0] is the [CardView] of the [Card] with ID 0
     */
    val allCards = mutableListOf<CardView>()

    /**
     * It keeps the hand card held by each player
     */
   // private val playerHandCard = mutableListOf<CardView?>()

    var theSelectedCardCanBePlace = false
        set(value) {
            field = value
            updatePlaceCardButton()
        }

    private fun updatePlaceCardButton() {
        if (selectedCardSlot != null && selectedCardSlot!!.cardView != null) {
            selectedCardSlot = null
        }
        placeCardButton.isDisabled = !(theSelectedCardCanBePlace && selectedCardSlot != null)
    }

    var selectedCardSlot:CardSlot? = null
        set(value) {
            if (field != null) {
                if (!field!!.isSet) {
                    field!!.frontVisual = if (field!!.isMouseEntered) {
                        ColorVisual(0, 0, 0, 25)
                    }
                    else {
                        ColorVisual.TRANSPARENT
                    }
                }
                if (field == value) {
                    field = null
                    updatePlaceCardButton()
                    return
                }
            }
            if (value != null) {
                value.frontVisual = CompoundVisual(
                    ColorVisual(0, 0, 0, 25),
                    ImageVisual(ImageIO.read(CardImageLoader
                        ::class.java.getResource("/targetVisual.png")))
                    )
            }
            field = value
            updatePlaceCardButton()
        }

    init {
        allCards.clear()
        for (i in 0..59) {
            allCards.add(CardView(
                front = ImageVisual(cardImageLoader.frontImageFor(i)),
                back = ImageVisual(cardImageLoader.backImage)
            ))
        }
    }

    /**
     * This method initializes the class [ViewData], which has the following effect:
     * - resets all [CardView]s stored in allCards
     * - clears the player's hand card.
     */
    fun init() {
        allCards.forEach {
            it.rotation = 0.0
        }
        selectedCardSlot = null
    }
}