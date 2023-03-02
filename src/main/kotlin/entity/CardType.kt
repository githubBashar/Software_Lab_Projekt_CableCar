package entity

/**
 * Specifies the card type
 *   @property POWER_STATION The power station in the middle of the playing field
 *   @property EMPTY An empty card
 *   @property TRAFFIC A rail card
 */
enum class CardType {
    POWER_STATION,
    EMPTY,
    TRAFFIC,
}