package service

import entity.Card
import entity.CardType

/**
 *  this service is for the undo/redo option of the game
 *
 *  @param rootService connection to [RootService]
 */
class UndoRedoService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * undo set the game back to the last move if such one exist
     */
    fun undo() {
        val game = rootService.mainGame
        checkNotNull(game)
        if (game.field.lastTurns.size != 0) {
            val currentPlayer = game.currentPlayer
            game.currentPlayer = (currentPlayer + game.players.size - 1) % game.players.size
            val lastTurn = game.field.lastTurns.pop()
            game.field.futureTurns.push(lastTurn)
            val card = game.field.fieldCards[lastTurn.posX][lastTurn.posY]
            card.angle = card.angle - lastTurn.angle
            val emptyCard = Card(-2, CardType.EMPTY, HashMap(), 0)
            game.field.fieldCards[(lastTurn.posX)][lastTurn.posY] = emptyCard
            if (lastTurn.isHandCard) {
                if(game.players[game.currentPlayer].handCards.isNotEmpty()){
                    game.drawPile.add(0, game.players[game.currentPlayer].handCards.first())
                    game.players[game.currentPlayer].handCards.removeFirst()
                }
                game.players[game.currentPlayer].handCards.add(card)
            } else
                game.drawPile.add(0, card)
            game.players.forEach { it.points = it.points - lastTurn.pointsDifferences[game.players.indexOf(it)] }
            lastTurn.trains.forEach { game.field.trains[it] = Pair(game.field.trains[it].first, false) }
        }
        onAllRefreshables { refreshAfterUndo() }
    }

    /**
     * redo set the game back to the next move after undo if such one exist
     */
    fun redo() {
        val game = rootService.mainGame
        checkNotNull(game)
        if (game.field.futureTurns.size != 0) {
            val currentPlayer = game.currentPlayer
            game.currentPlayer = (currentPlayer + game.players.size + 1) % game.players.size
            val nextTurn = game.field.futureTurns.pop()
            game.field.lastTurns.push(nextTurn)
            val card = game.field.fieldCards[nextTurn.posX][nextTurn.posY]
            card.angle = card.angle - nextTurn.angle
            if (nextTurn.isHandCard) {
                //game.field.fieldCards[(nextTurn.posX)].add(nextTurn.posY, game.players[game.currentPlayer].handCards[0])
                game.field.fieldCards[(nextTurn.posX)][nextTurn.posY] = game.players[game.currentPlayer].handCards[0]
                game.players[game.currentPlayer].handCards.removeFirst()
                game.players[game.currentPlayer].handCards.add(game.drawPile[0])
                game.drawPile.removeFirst()
            } else
                game.field.fieldCards[(nextTurn.posX)][nextTurn.posY] = game.drawPile[0]
            game.drawPile.removeFirst()
            game.players.forEach { it.points = it.points + nextTurn.pointsDifferences[game.players.indexOf(it)] }
            nextTurn.trains.forEach { game.field.trains[it] = Pair(game.field.trains[it].first, true) }
        }
        onAllRefreshables { refreshAfterRedo() }
    }
}