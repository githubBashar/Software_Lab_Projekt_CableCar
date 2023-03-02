package service

import entity.Card
import entity.CardType
import entity.MainGame
import service.minimax.State

/**
 * This Service checks positions for legality
 *
 * @param rootService The connection to [RootService].
 * @property errorMemory Saves the error description. TRAFFIC = There is no traffic-card beside. POINTS = There is one
 * train just gain one point and another way to place the card. EMPTY = The place is not empty
 */
class LegalService (private val rootService: RootService): AbstractRefreshingService(){

    var errorMemory = ""

    /**
     * Checks the given position for legality
     *
     * @param posX target´s x-Position
     * @param posY target´s y-Position
     * @return true if the player can place the card with the actual angle on the given position
     */
    fun checkLegality(posX: Int, posY: Int, state: State? = null): Boolean {
        val mG = rootService.mainGame
        //get the current Game
        checkNotNull(mG) { "There is no game" }
        val mGF = state?.field ?: mG.field.fieldCards

        //error handling
        posError(posX, Pair(0, 7), posY, Pair(0, 7))

        if (mGF[posX][posY].cardType == CardType.EMPTY && posX % 7 != 0 && posY % 7 != 0) {
            return if (nextToCheck(posX, posY)) {
                true
            } else {
                errorMemory = "TRAFFIC"
                false
            }
        } else if (mGF[posX][posY].cardType == CardType.EMPTY) {
            return if (noOnePointWays(posX, posY)) {
                true
            } else {
                //Check for any other possibility
                if (anyPossibleWay()) {
                    errorMemory = "POINTS"
                    false
                } else {
                    true
                }
            }
        } else {
            errorMemory = "EMPTY"
            return false
        }
    }

    /**
     * Check if there is a TRAFFIC-Card next to the position.
     *
     * The place can not be at the border.
     *
     * @param posX x-Position for the target position. It has to be 0 < posX < 8
     * @param posY y-Position for the target position. It has to be 0 < posY < 8
     */
    fun nextToCheck(posX: Int, posY: Int, state: State? = null): Boolean {
        //get the current game
        val mG = rootService.mainGame
        checkNotNull(mG) { "no game available" }
        val mGF = state?.field ?: mG.field.fieldCards

        posError(posX, Pair(1, 6), posY, Pair(1, 6))
        if (mGF[posX + 1][posY].cardType == CardType.TRAFFIC)
            return true

        if (mGF[posX - 1][posY].cardType != CardType.TRAFFIC) {
            if (mGF[posX][posY + 1].cardType != CardType.TRAFFIC) {
                if (mGF[posX][posY - 1].cardType != CardType.TRAFFIC) {
                    return false
                }
            }
        }
        return true
    }

    //-------------- Private functions ----------------------------------------------------------------------------

    /**
     * Check if one train only gains one point.
     *
     * @param posX x-Position for the target position.
     * @param posY y-Position for the target position.
     * @return true if there are no one point trains.
     */
    private fun noOnePointWays(posX: Int, posY: Int, state: State? = null): Boolean {
        //get the current game
        val mG = rootService.mainGame
        checkNotNull(mG) { "no game available" }
        val mGP = state?.players ?: mG.players
        val mGF = state?.field ?: mG.field.fieldCards
        val cP = state?.currentPlayer ?: mG.currentPlayer

        //Place the card for the check
        check(mGP[cP].handCards.size > 0) { "The player has no handCard" }
        mGF[posX][posY] = mGP[cP].handCards.last()
        val score = rootService.scoreService.computeScore()

        //Check if one train only gains one point
        for (i in 0 until score.size) {
            if (score[i].second == 1) {
                //Remove the card
                mGF[posX][posY] = Card(-1, CardType.EMPTY, hashMapOf(), 0)
                return false
            }
        }
        //Remove the card
        mGF[posX][posY] = Card(-1, CardType.EMPTY, hashMapOf(), 0)
        return true
    }

    /**
     * Check if it´s possible to place the card at another place
     *
     * This function places the playercard at all places until there is one legal possibility.
     * If it´s not possible, the function return false
     */
    private fun anyPossibleWay(state: State? = null): Boolean {
        //get the current game
        val mG = rootService.mainGame
        checkNotNull(mG) { "no game available" }

        val freeFields =
            if (state != null) rootService.gameService.getFreeFields(state) else rootService.gameService.getFreeFields()
        val helpAngel = state?.players?.get(state.currentPlayer)?.handCards?.last()?.angle
            ?: mG.players[mG.currentPlayer].handCards.last().angle

        //Für alle freien Felder
        for (i in freeFields.indices) {
            if (mG.rotatable) {
                if (allAngel(freeFields, i, mG, helpAngel, state)) return true
            } else {
                if (noOnePointWays(freeFields[i].first, freeFields[i].second, state)) {
                    return true
                }
            }
        }
        return false
    }
    private fun allAngel(
        freeFields: MutableList<Pair<Int, Int>>,
        i: Int,
        mainGame: MainGame,
        helpAngel: Int,
        state: State? = null
    ): Boolean {
        val mGP = state?.players ?: mainGame.players
        val cP = state?.currentPlayer ?: mainGame.currentPlayer
        repeat(4) {
            mGP[cP].handCards.last().angle += 90
            mGP[cP].handCards.last().angle %= 360
            if (noOnePointWays(freeFields[i].first, freeFields[i].second, null)) {
                mGP[cP].handCards.last().angle = helpAngel
                return true
            }
        }
        return false
    }

    private fun posError(posX: Int, xRange : Pair<Int,Int> , posY: Int , yRange : Pair<Int,Int>) {
        if (posX > xRange.second || posX < xRange.first) throw IllegalArgumentException("illegal x-Position")
        if (posY > yRange.second || posY < yRange.first) throw IllegalArgumentException("illegal y-Position")
    }

}