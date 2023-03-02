package service.minimax

import entity.Card
import entity.CardType
import entity.Player
import service.RootService

/**
 * The State class represents the current state of the game.
 * It contains information about the draw pile, players, field, trains, current player and rotatable
 *
 * @property rootService RootService object that is used to access services and data of the main game
 * @property state State object that is used to copy the current state
 * @property drawPile MutableList of Card objects representing the draw pile
 * @property players MutableList of Player objects representing the players
 * @property field MutableList of MutableList of Card objects representing the field
 * @property trains MutableList of Pair<Int, Boolean> representing the trains
 * @property currentPlayer Int representing the index of the current player
 * @property rotatable Boolean representing whether rotation is enabled or not
 */

data class State(val rootService: RootService, val state: State?) {
    var drawPile = mutableListOf<Card>()
    var players = mutableListOf<Player>()
    var field = mutableListOf<MutableList<Card>>()
    var trains = mutableListOf<Pair<Int, Boolean>>()
    var currentPlayer = 0
    var rotatable = false


    /**
     * Initializes the State object. If state is null, it initializes the state with the data from the main game.
     * If state is not null, it copies the data from the state object passed
     */

    init {
        if (state == null) {
            drawPile = rootService.mainGame!!.drawPile.map { it.copy() }.toMutableList()
            players = rootService.mainGame!!.players.map {
                Player(it.name, it.handCards.map { it.copy() }.toMutableList(), it.playerType).apply {
                    points = it.points
                }
            }.toMutableList()
            field = rootService.mainGame!!.field.fieldCards.map { it.toMutableList() }.toMutableList()
            trains = rootService.mainGame!!.field.trains.map { it.copy() }.toMutableList()
            currentPlayer = rootService.mainGame!!.currentPlayer
            rotatable = rootService.mainGame!!.rotatable
        } else {

            drawPile = state.drawPile.map { it.copy() }.toMutableList()
            players = state.players.map {
                Player(it.name, it.handCards.map { it.copy() }.toMutableList(), it.playerType).apply {
                    points = it.points
                }
            }.toMutableList()
            field = state.field.map { it.toMutableList() }.toMutableList()
            trains = state.trains.toMutableList()
            currentPlayer = state.currentPlayer
            rotatable = state.rotatable
        }
    }

    /**
     * Returns the new state after the action is performed on the current state
     *
     * @param state Current State object
     * @param action Action object representing the action to be performed
     * @return State object representing the new state after applying the action

     */
    fun result(state: State, action: Action): State {
        val newState = State(rootService, state)
        if (!action.draw) {

            // rotate tile if needed
            state.players[state.currentPlayer].handCards[0].angle = (action.rotated * 90) % 360
            // tile from hand
            val tile = newState.players[newState.currentPlayer].handCards[0]


            // put tile onto Field
            newState.field[action.posX][action.posY] = tile

            // give player new tile from tileStack
            if (newState.drawPile.isNotEmpty()) {
                newState.players[newState.currentPlayer].handCards[0] = state.drawPile.random()
                newState.drawPile.remove(newState.players[newState.currentPlayer].handCards[0])
            }
            rootService.playerService.safeGameTurn(0, 0, newState)
            newState.currentPlayer = (state.currentPlayer + 1) % state.players.size
        } else {

            val tile = newState.drawPile.random()
            newState.players[newState.currentPlayer].handCards.add(tile)
            newState.drawPile.remove(tile)


        }
        return newState
    }

    /**
     * Returns a list of all legal plays that can be made by the current player.
     * @param state The current state of the game.
     * @return A list of legal plays that can be made by the current player.
     */
    fun legalActions(state: State): MutableList<Action> {
        val plays = mutableListOf<Action>()
        val handCard = state.players[state.currentPlayer].handCards[0]
        val freeFiled = rootService.gameService.getFreeFields(state)
        for (i in freeFiled) {
            val x = i.first
            val y = i.second
            if (!state.rotatable) {
                if (rootService.legalService.checkLegality(x, y, state)) {
                    plays.add(Action(false, 0, x, y))
                }
            } else {
                if (rootService.legalService.checkLegality(x, y, state)) {
                    plays.add(Action(false, 0, x, y))
                }
                handCard.angle += 90
                handCard.angle %= 360
                if (rootService.legalService.checkLegality(x, y, state)) {
                    plays.add(Action(false, 1, x, y))
                }
                handCard.angle += 90
                handCard.angle %= 360
                if (rootService.legalService.checkLegality(x, y, state)) {
                    plays.add(Action(false, 2, x, y))
                }
                handCard.angle += 90
                handCard.angle %= 360
                if (rootService.legalService.checkLegality(x, y, state)) {
                    plays.add(Action(false, 3, x, y))
                }
                handCard.angle = 0

            }

        }

        if (state.players[state.currentPlayer].handCards.size < 2 && state.drawPile.size > 0) {
            plays.add(Action(true, 0, -1, -1))
        }

        return plays
    }


    /*fun legalActions1(state: State): MutableList<Action> {
        val plays = mutableListOf<Action>()
        for (i in rootService.gameService.getFreeFields(state)){
            if (state.rotatable){
                for (j in 0..3){
                    val tmp = rootService.legalService.checkLegality(state,i.first,i.second)
                    if (tmp.first)
                        plays.add(Action(false,j,i.first,i.second,tmp.second))
                    state.players[state.currentPlayer].handCards[0].angle += 90
                    state.players[state.currentPlayer].handCards[0].angle %= 360
                }
            }
            else{
                val tmp = rootService.legalService.checkLegality(state,i.first,i.second)
                if (tmp.first)
                    plays.add(Action(false,0,i.first,i.second,tmp.second))
            }
        }
        if (state.players[currentPlayer].handCards.size < 2 && state.drawPile.size > 0)
            plays.add(Action(true, 0, -1, -1, "draw"))
        return plays
    }*/


    /**
     * Calculates the heuristic value for a given game state.
     *
     * @param state The current state of the game.
     * @return A double representing the heuristic value of the state.
     */

    fun heuristicValue(state: State): Double {
        val score: Double
        val playersPoints = mutableListOf<Int>()
        for (playerIdx in state.players.indices) {
            if (playerIdx != state.currentPlayer)
                playersPoints.add(paths(state, playerIdx))
        }
        playersPoints.sortDescending()
        score = paths(state, state.currentPlayer).toDouble() - playersPoints[0].toDouble()
        return score
    }


    private fun paths(state: State, playerIndex: Int): Int {
        val map1 = mapOf(
            1 to listOf(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31),
            2 to listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32)
        )
        val map2 = mapOf(
            1 to listOf(1, 4, 6, 11, 15, 20, 23, 25, 28, 31),
            2 to listOf(2, 7, 9, 12, 14, 19, 22, 27, 29, 32),
            3 to listOf(3, 5, 8, 10, 13, 18, 21, 24, 26, 30)
        )
        val map3 = mapOf(
            1 to listOf(4, 7, 11, 16, 20, 23, 27, 32),
            2 to listOf(3, 8, 12, 15, 19, 24, 28, 31),
            3 to listOf(1, 6, 10, 13, 18, 21, 25, 30),
            4 to listOf(2, 5, 9, 14, 17, 22, 26, 29)
        )
        val map4 = mapOf(
            1 to listOf(1, 5, 10, 14, 22, 28),
            2 to listOf(6, 12, 18, 23, 27, 32),
            3 to listOf(3, 7, 15, 19, 25, 29),
            4 to listOf(2, 9, 13, 21, 26, 30),
            5 to listOf(4, 8, 11, 20, 24, 31)
        )
        val map5 = mutableMapOf(
            1 to listOf(1, 5, 10, 19, 27),
            2 to listOf(2, 11, 18, 25, 29),
            3 to listOf(4, 8, 14, 21, 26),
            4 to listOf(6, 15, 20, 24, 31),
            5 to listOf(3, 9, 13, 23, 30),
            6 to listOf(7, 12, 22, 28, 32)
        )
        val mapOfMaps = mapOf(2 to map1, 3 to map2, 4 to map3, 5 to map4, 6 to map5)
        var tmp = 0
        for (trainIdx in mapOfMaps[state.players.size]!![playerIndex + 1]!!) {
            if (trainIdx < 8) {
                tmp += getPathPoints(trainIdx % 8, 0, 0)
            } else if (trainIdx < 16) {
                tmp += getPathPoints(7, trainIdx % 8, 2)
            } else if (trainIdx < 24) {
                tmp = +getPathPoints(7 - (trainIdx % 8), 7, 4)
            } else {
                tmp += getPathPoints(0, 7 - (trainIdx % 8), 6)
            }
        }
        return tmp
    }

    private fun getPathPoints(posX: Int, posY: Int, port: Int): Int {

        val mG = state


        var card = mG!!.field[posX][posY]
        var points = 0
        var tmpPort = port
        var tPosX = posX
        var tPosY = posY

        //Iterate through the path
        while (card.cardType == CardType.TRAFFIC) {
            tmpPort = card.getWay(tmpPort)
            points++

            //Calculate, which way next
            when (tmpPort) {
                0, 1 -> tPosY++
                2, 3 -> tPosX--
                4, 5 -> tPosY--
                6, 7 -> tPosX++
            }
            //Check if the train is over the edge
            val notLeftVerticalLimit = tPosY != -1 && tPosY != 8
            val notLeftHorizontalLimit = tPosX != -1 && tPosX != 8

            if(notLeftVerticalLimit && notLeftHorizontalLimit){
                card = mG.field[tPosX][tPosY]
            } else {
                return points
            }
        }
        //Only powerstation or empty card left
        return when (card.cardType) {
            CardType.POWER_STATION -> points * 2
            CardType.EMPTY -> points
            else -> 0
        }
    }
}




