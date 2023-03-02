package entity

/**
 * An object of class [GameTurn] stores information about a game turn.
 *  @param [isHandCard] Was the placed card in hand (= true) or drawn extra (= false).
 *  @param [angle] Rotation of the card (0, 90, 180, 270).
 *  @param [posX] The x-position of the laid card (1st index at [GameBoard]).
 *  @param [posY] The y-position of the laid card (2nd index at [GameBoard]).
 *  @param [pointsDifferences] Stores the point differences.
 *  @param [trains] Stores the indices of all trains that ran after the train.
 */
class GameTurn(val isHandCard: Boolean,
               val angle:Int,
               val posX:Int, val posY: Int,
               val pointsDifferences: MutableList<Int>,
               val trains:MutableList<Int>)
