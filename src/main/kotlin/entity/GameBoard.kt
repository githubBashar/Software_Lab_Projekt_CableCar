package entity

import java.util.*

/**
 * This class manages the playing field
 * @param [fieldCards] Stores which card is where. If there is no card at one point = emptyCard.
 * @param [lastTurns] Stores the previous turns for the undo() function.
 * @param [futureTurns] Saves when moves have been undone
 * which were for the redo() function. Has the same structure as lastTurn.
 * @param [trains] FIRST → Index of the associated player (Int). SECOND → Has the train already left (= true) or
 *  not (= false)
 */
class GameBoard(
    var fieldCards:MutableList<MutableList<Card>>,
    val lastTurns: Stack<GameTurn>,
    var futureTurns:Stack<GameTurn>,
    val trains:MutableList<Pair<Int,Boolean>>)