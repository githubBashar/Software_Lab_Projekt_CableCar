package service

import entity.CardType
import service.minimax.State

/**
 * The ScoreService compute the Points
 *
 * @param rootService The connection to the [RootService]
 */
class ScoreService (private val rootService: RootService){

    /**
     * This method computes the Points after a card is dropped.
     *
     * @return A List of Pair<Int, Int>. FIRST represent the index of the driven Train,
     * SECOND the points which the train gains.
     */
    fun computeScore(state: State? = null): MutableList<Pair<Int, Int>> {

        val mG = rootService.mainGame
        checkNotNull(mG) { "Theres no game" }
        val fieldCards = state?.field ?: mG.field.fieldCards
        val trains = state?.trains ?: mG.field.trains
        val returnValue: MutableList<Pair<Int, Int>> = mutableListOf()

        //Error handling
        check(trains.size == 32) { "There aren´t 32 trains" }
        check(fieldCards.size == 8) { "There aren´t 8 slots in x direction" }
        for (i in 0..7) {
            check(fieldCards[i].size == 8) { "There aren´t 8 cards in Line $i" }
        }

        //Check all trains
        var tmp: Int
        for (i in 0 until 32) {
            //Does the train hasn´t left yet?
            if (!trains[i].second) {
                //Get the Points for the way
                if (i < 8) {
                    tmp = getPathPoints(i % 8, 0, 0)
                } else if (i < 16) {
                    tmp = getPathPoints(7, i % 8, 2)
                } else if (i < 24) {
                    tmp = getPathPoints(7 - (i % 8), 7, 4)
                } else {
                    tmp = getPathPoints(0, 7 - (i % 8), 6)
                }
                if (tmp != 0) {
                    returnValue.add(Pair(i, tmp))
                }
            }
        }
        return returnValue
    }

    /**
     * Get the points for one way
     *
     * @param posX X-position from the start card
     * @param posY Y-position from the start card
     * @param port Enter port
     */
    private fun getPathPoints(posX: Int, posY: Int, port: Int): Int {

        val mG = rootService.mainGame
        checkNotNull(mG)
        val mGF = mG.field

        var card = mGF.fieldCards[posX][posY]
        var points = 0
        var tmpPort = port
        var tPosX = posX
        var tPosY = posY

        //Iterate through the path
        while(card.cardType == CardType.TRAFFIC){
            tmpPort = card.getWay(tmpPort)
            points++

            //Calculate, which way next
            when (tmpPort){
                0,1 -> tPosY++
                2,3 -> tPosX--
                4,5 -> tPosY--
                6,7 -> tPosX++
            }
            //Check if the train is over the edge
            val notLeftVerticalLimit = tPosY != -1 && tPosY != 8
            val notLeftHorizontalLimit = tPosX != -1 && tPosX != 8

            if(notLeftVerticalLimit && notLeftHorizontalLimit){
                card = mGF.fieldCards[tPosX][tPosY]
            }else{
                return points
            }
        }
        //Only powerstation or empty card left
        return when (card.cardType) {
            CardType.POWER_STATION -> points * 2
            CardType.EMPTY -> 0
            else -> 0
        }
    }

}
