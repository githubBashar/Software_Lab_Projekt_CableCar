package view

import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.visual.ColorVisual
import entity.*

/**
 * This class contains the layout of trains. Each train is actually a [CardView],
 * The [CardView.frontVisual] of the [CardView] will be displayed when the train
 * is not departing. Contrary to this, the [CardView.backVisual] will be displayed
 * after the departure of the train.
 */
class Trains: Pane<CardView>(
    posX = 1156, posY = 0,
    width = 3112, height = 2995) {

    private val trainCards = mutableListOf<CardView>()

    init {
        for (i in 0 until 32) {
            val sideScopeIndex = i % 8
            when (i / 8) {
                0 -> {
                    val train = CardView(
                        posX = 520 + 264 * sideScopeIndex,
                        posY = 330,
                        width = 122, height = 70,
                        front = ColorVisual.TRANSPARENT
                    ).apply {
                        rotation = 90.0
                    }
                    this.trainCards.add(train)
                    this.addAll(train)
                }
                1 -> {
                    val train = CardView(
                        posX = 2650,
                        posY = 490  + 264 * sideScopeIndex,
                        width = 122, height = 70,
                        front = ColorVisual.TRANSPARENT
                    ).apply {
                        rotation = 180.0
                    }
                    this.trainCards.add(train)
                    this.addAll(train)
                }
                2 -> {
                    val train = CardView(
                        posX = 2470 - 264 * sideScopeIndex,
                        posY = 2605,
                        width = 122, height = 70,
                        front = ColorVisual.TRANSPARENT
                    ).apply {
                        rotation = 270.0
                    }
                    this.trainCards.add(train)
                    this.addAll(train)
                }
                3 -> {
                    val train = CardView(
                        posX = 340 ,
                        posY = 2440 - 264 * sideScopeIndex,
                        width = 122, height = 70,
                        front = ColorVisual.TRANSPARENT
                    ).apply {
                        rotation = 0.0
                    }
                    this.trainCards.add(train)
                    this.addAll(train)
                }
            }
        }
    }

    /**
     * This method will update the appearance of trains (change the color of the trains
     * & flip the card if necessary) at the beginning of the game.
     *
     * @param trains configuration of trains
     * @see [GameBoard]
     *
     * @throws IllegalArgumentException if the [trains] too lang
     */
    fun updateAfterStartGame(trains: MutableList<Pair<Int, Boolean>>) {
        for (i in trains.indices) {
            val visual = PlayerColor.getTrainImage(trains[i].first)
            val target = getTrains(i)
            target.frontVisual = visual.first
            target.backVisual = visual.second
            if (trains[i].second) {
                target.showBack()
            }
            else {
                target.showFront()
            }
        }
    }

    /**
     * This method will update the appearance of trains (flip the card of trains) after the end of turns.
     *
     * @param trains configuration of trains
     * @see [GameBoard]
     *
     * @throws IllegalArgumentException if the [trains] too lang
     */
    fun updateAfterTurnEnd(trains: MutableList<Pair<Int, Boolean>>) {
        for (i in trains.indices) {
            val target = getTrains(i)
            if (trains[i].second) {
                target.showBack()
            }
            else {
                target.showFront()
            }
        }
    }

    /**
     * Get the train object by index.
     *
     * @param index the index of the target train.
     * @return the target train [CardView] object.
     *
     * @throws IllegalArgumentException when the index out of bound.
     */
    private fun getTrains(index: Int): CardView {
        if (index < 0 || index > 31) throw IllegalArgumentException(
            "The index of trains should between 0 and 31, but current value is $index."
        )
        return this.trainCards[index]

        /*if (index < 0 || index > 31) throw IllegalArgumentException(
            "The index of trains should between 0 and 31, but current value is $index."
        )
        val side = index / 8
        val indexOfSide = index % 8
        return when (side) {
            0 -> this[indexOfSide + 1, 0]!!
            1 -> this[9, indexOfSide + 1]!!
            2 -> this[8 - indexOfSide, 9]!!
            3 -> this[0, 8 - indexOfSide]!!
            else -> throw IllegalStateException()
        }*/
    }
}
