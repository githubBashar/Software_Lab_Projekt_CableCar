package service

import entity.*
import service.minimax.State

/**
 * Manages the player's actions
 *
 * @param rootService The connection to [RootService]
 * @property secondCard Has the actualPlayer just one handCard (= false) or a second card (= true)?
 */
class PlayerService (private val rootService: RootService): AbstractRefreshingService(){

    var secondCard = false

    /**
     * Draw a card
     *
     * Set [secondCard] = true if the player draws a second card
     * @throws IllegalArgumentException When there are no cards to draw or when the player has already 2 cards
     */
    fun drawNewCard(){
        //get the current game
        val game = rootService.mainGame
        checkNotNull(game) {"no game available"}

        //Check if there are cards left to draw
        if(game.drawPile.isNotEmpty()) {
            //Check if the player has not got 2 cards already
            if(!secondCard){
                game.players[game.currentPlayer].handCards.add(game.drawPile.first())
                game.drawPile.removeFirst()
                secondCard = (game.players[game.currentPlayer].handCards.size == 2)
            }else{
                throw IllegalStateException("The player just have 2 cards. He cant pull one more")
            }
        }else{
            throw IllegalStateException("drawPile is empty")
        }
        onAllRefreshables { refreshAfterDraw() }
    }

    /**
     * Places the selected card in the appropriate position
     *
     * This function doesn't change secondCard
     *
     * @param posX x-Position
     * @param posY y-Position
     * @throws IllegalArgumentException When it´s illegal to place the card there
     */
    fun placeCard(posX: Int, posY: Int) {
        //get the current game
        val mG = rootService.mainGame
        checkNotNull(mG) { "no game available" }
        //smart-casts
        val field = mG.field.fieldCards
        val players = mG.players

        //Check legality of this position
        if (rootService.legalService.checkLegality(posX, posY)) {
            field[posX][posY] = players[mG.currentPlayer].handCards.last()
            players[mG.currentPlayer].handCards.removeLast()
            if (!secondCard && rootService.mainGame!!.drawPile.isNotEmpty()) drawNewCard()
            safeGameTurn(posX, posY)
        } else {
            throw IllegalArgumentException(
                "You cant place that card there. Failure code: "
                        + rootService.legalService.errorMemory
            )
        }
    }

    /**
     * Spins the player card
     */
    fun rotate(){
        val game = rootService.mainGame
        checkNotNull(game){"Theres no game"}
        if(game.rotatable){
            game.players[game.currentPlayer].handCards.last().angle =
                (game.players[game.currentPlayer].handCards.last().angle + 90) % 360
        }else{
            throw IllegalStateException("rotate() is not activated for this game")
        }
        onAllRefreshables { refreshAfterRotate() }
    }

    //TODO Aufräumen
    /**
     * Management of all queries when switching players
     *
     * currentPlayer ++,
     * secondCard = false,
     * Check if the game is over
     */
    fun nextPlayer(){
        //get the current game
        val game = rootService.mainGame
        checkNotNull(game) {"no game available"}
        if (rootService.networkService.client == null) {
            if (game.players[game.currentPlayer].playerType == PlayerType.HUMAN) {
                game.currentPlayer = (game.currentPlayer + 1) % game.players.size
            }
            secondCard = false

            game.field.futureTurns.clear()
            //Check if the game is over
            if (rootService.gameService.getFreeFields().isEmpty()) {
                rootService.gameService.endGame()
                onAllRefreshables { refreshAfterEndGame() }
            }
            val curType = game.players[game.currentPlayer].playerType

            if (curType == PlayerType.HARDAI || curType == PlayerType.EASYAI)
                if (rootService.gameService.getFreeFields().isEmpty()) {
                    rootService.gameService.endGame()
                    onAllRefreshables { refreshAfterEndGame() }
                    return
                }
                if (curType == PlayerType.EASYAI) {
                    rootService.aiService.randomTurn()
                    secondCard = false
                    game.currentPlayer = (game.currentPlayer + 1) % game.players.size
                    game.field.futureTurns.clear()
                }
                if (curType == PlayerType.HARDAI) {
                    rootService.aiService.smartTurn()
                    secondCard = false
                    game.currentPlayer = (game.currentPlayer + 1) % game.players.size
                    game.field.futureTurns.clear()
                }
            onAllRefreshables { refreshAfterTurnEnds() }
        } else {
            // Network
            val client = rootService.networkService.client
            if (client!!.isEasyAI || client.isHardAI) {
                game.currentPlayer = (game.currentPlayer + 1) % game.players.size
                if (rootService.gameService.getFreeFields().isEmpty()) {
                    rootService.gameService.endGame()
                    onAllRefreshables { refreshAfterEndGame() }
                    return
                }
                if (client.isEasyAI) {
                    if (rootService.networkService.connectionState == ConnectionState.PLAYING_TURN) {
                        isEasyAi()
                    }
                }
                else if (client.isHardAI) {
                    if (rootService.networkService.connectionState == ConnectionState.PLAYING_TURN) {
                        val game = rootService.mainGame
                        checkNotNull(game) {"no game available"}

                        rootService.aiService.smartTurn()
                        secondCard = false

                        game.field.futureTurns.clear()
                        rootService.networkService.sendTurnMessage(rootService.mainGame!!.field.lastTurns.peek())
                        rootService.networkService.updateConnectionState(ConnectionState.WAITING_FOR_TURN)
                        if (rootService.gameService.getFreeFields().isEmpty()) {
                            rootService.gameService.endGame()
                            onAllRefreshables { refreshAfterEndGame() }
                        }
                        game.currentPlayer = (game.currentPlayer + 1) % game.players.size
                    }
                }

                // Hard AI was not considered but it is identical
            onAllRefreshables { refreshAfterTurnEnds() }
            }
            else {
                notAi()
            }
        }
    }

    //--------------------------------- Private functions ----------------------------------------------------

    private fun isEasyAi() {
        //get the current game
        val game = rootService.mainGame
        checkNotNull(game) {"no game available"}
        secondCard = false

        rootService.aiService.randomTurn()

        game.field.futureTurns.clear()
        rootService.networkService.sendTurnMessage(rootService.mainGame!!.field.lastTurns.peek())
        rootService.networkService.updateConnectionState(ConnectionState.WAITING_FOR_TURN)
        if (rootService.gameService.getFreeFields().isEmpty()) {
            rootService.gameService.endGame()
            onAllRefreshables { refreshAfterEndGame() }
        }
        game.currentPlayer = (game.currentPlayer + 1) % game.players.size
    }
    private fun notAi() {
        //get the current game
        val game = rootService.mainGame
        checkNotNull(game) {"no game available"}
        // the player is not an AI
        game.currentPlayer = (game.currentPlayer + 1) % game.players.size
        secondCard = false
        game.field.futureTurns.clear()
        //Check if the game is over
        if (rootService.gameService.getFreeFields().isEmpty()) {
            rootService.gameService.endGame()
            onAllRefreshables { refreshAfterEndGame() }
        }
        onAllRefreshables { refreshAfterTurnEnds() }
    }

    /**
     * Safe the gameTurn
     */
    fun safeGameTurn(posX: Int, posY: Int, state: State? = null) {
        //get the current game

        val mG = rootService.mainGame
        checkNotNull(mG) { "no game available" }
        //smart-casts
        val field = state?.field ?: mG.field.fieldCards
        val players = state?.players ?: mG.players
        val trains = state?.trains ?: mG.field.trains

        //prepare everything
        val trainPoints =
            if (state != null) rootService.scoreService.computeScore(state) else rootService.scoreService.computeScore()
        val trainList = mutableListOf<Int>()
        val playerPoints = mutableListOf(0)
        for (i in 0 until mG.players.size - 1) {
            playerPoints.add(0)
        }

        //add the points
        for (i in 0 until trainPoints.size) {
            if (trains[trainPoints[i].first].first != 7) {
                trains[trainPoints[i].first] = trains[trainPoints[i].first].first to true
                trainList.add(trainPoints[i].first)
                playerPoints[trains[trainPoints[i].first].first - 1] += trainPoints[i].second
                players[trains[trainPoints[i].first].first - 1].points += trainPoints[i].second
            }
        }
        //safe the GameTurn
        if (state == null) {
            mG.field.lastTurns.add(
                GameTurn(!secondCard, field[posX][posY].angle, posX, posY, playerPoints, trainList)
            )
        }
    }
}
