package service


import entity.CardType
import service.minimax.State

import service.minimax.AiAlgorithm
import java.lang.Integer.max


/**
 * Der KI Service kümmert sich um die Berechnungen der KI
 *
 * @param rootService Die Verbindung zum [RootService]
 */
class AiService(private val rootService: RootService) : AbstractRefreshingService() {
    /**
     * Determines if the game is over by checking if the game board is full or if the draw pile is empty
     *
     * @param state the current state of the game
     * @return true if the game is over, false otherwise
     */
    fun isGameOver(state: State): Boolean {
        var isFieldFull = true

        for (row in 0 until state.field.size) {
            for (cell in 0 until state.field[row].size) {
                if (state.field[row][cell].cardType == CardType.EMPTY) {
                    isFieldFull = false
                    break
                }
            }
        }
        return isFieldFull || state.drawPile.isEmpty()
    }




    /**
     * Makes a random move
     */
    fun randomTurn(){
       // val isNetworkGame = rootService.networkService.client != null

        val freeFields = rootService.gameService.getFreeFields()
        freeFields.shuffle()
        var i = 0
        while (freeFields.isNotEmpty()){
            if (rootService.legalService.checkLegality(freeFields[i].first , freeFields[i].second)){
                rootService.playerService.placeCard(freeFields[i].first , freeFields[i].second)
                return
            }
            else{
                i++
            }
        }

    }

    /**
     * Executes the best move that the AI calculates.
     */
    fun smartTurn() {
        val state = State(rootService, null)
        val x = state.players.size * rootService.gameService.getFreeFields().size
        val depth = max(
            208 / (x * if (rootService.mainGame!!.rotatable) 4 else 1),
            2
        )
        var play = AiAlgorithm(rootService).bestAction(
            State(rootService, null),
            depth,
            rootService.mainGame!!.players.size
        )
        if (play!!.draw) {
            rootService.playerService.drawNewCard()
            play = AiAlgorithm(rootService).bestAction(
                State(rootService, null),
                depth,
                rootService.mainGame!!.players.size
            )
            for (rotate in 0 until play!!.rotated) { rootService.playerService.rotate() }
            rootService.playerService.placeCard(play.posX, play.posY)
        } else {
            for (rotate in 0 until play.rotated) {
                rootService.playerService.rotate()
            }
            rootService.playerService.placeCard(play.posX, play.posY)
        }
    }
}