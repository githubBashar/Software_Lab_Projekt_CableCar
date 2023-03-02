package entity

/**
 * An object of class [Player] stores all necessary information about a player.
 * @param [name] The name of the player.
 * @param [handCards] Each player gets a card in the hand.
 * @param [playerType] Whether the player is human or AI
 * @throws IllegalArgumentException If the player's name is blank
 */
class Player (val name: String,
              var handCards: MutableList<Card>,
              val playerType: PlayerType)
{
    var points = 0

    /**
     * Check if the player has been given a name
     */
    init {
        if (name == "") {
            throw IllegalArgumentException("The player has no name yet")
        }
    }

    /**
     * Name in string
     */
    override fun toString(): String = "$name: $points"
}