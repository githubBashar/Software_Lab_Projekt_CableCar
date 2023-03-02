package entity

/**
 * The data class MainGame stores the main game.
 * @param [players] The list of players.
 * @param [field] The field.
 * @param [drawPile] The draw pile.
 * @param [rotatable] Whether to rotate the card.
 * @property [currentPlayer] The index of the current player.
 * @property [simulationVit] The speed of the simulation.
 * */

data class MainGame(var drawPile:MutableList<Card>,
                    val players:MutableList<Player>,
                    val field:GameBoard,
                    var rotatable:Boolean
) {
    var currentPlayer = 0
    var simulationVit = 1.0
}