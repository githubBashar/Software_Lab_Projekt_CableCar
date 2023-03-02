package service

import entity.*
import service.minimax.State
import java.io.BufferedReader
import java.io.File
import java.util.*

/**
 * Service class for the game that provides the logic for the game
 *
 * @param rootService connection to [RootService]
 * @property drawPile safes the draw Pile before any card is dealt
 */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {
    private val map1 = mapOf(
        1 to listOf(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31),
        2 to listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32)
    )
    private val map2 = mapOf(
        1 to listOf(1, 4, 6, 11, 15, 20, 23, 25, 28, 31),
        2 to listOf(2, 7, 9, 12, 14, 19, 22, 27, 29, 32),
        3 to listOf(3, 5, 8, 10, 13, 18, 21, 24, 26, 30)
    )
    private val map3 = mapOf(
        1 to listOf(4,7,11,16,20,23,27,32),
        2 to listOf(3,8,12,15,19,24,28,31),
        3 to listOf(1,6,10,13,18,21,25,30),
        4 to listOf(2,5,9,14,17,22,26,29)
    )
    private val map4 = mapOf(
        1 to listOf(1,5,10,14,22,28),
        2 to listOf(6,12,18,23,27,32),
        3 to listOf(3,7,15,19,25,29),
        4 to listOf(2,9,13,21,26,30),
        5 to listOf(4,8,11,20,24,31)
    )
    private val map5 = mutableMapOf(
        1 to listOf(1,5,10,19,27),
        2 to listOf(2,11,18,25,29),
        3 to listOf(4,8,14,21,26),
        4 to listOf(6,15,20,24,31),
        5 to listOf(3,9,13,23,30),
        6 to listOf(7,12,22,28,32)
    )
    private val mapOfMaps = mapOf(2 to map1, 3 to map2, 4 to map3, 5 to map4, 6 to map5)

    var drawPile = mutableListOf<Card>()

    /**
     * starts the game
     *
     * @param players Names and Types of the Players.
     * @param rotatable states if the game has the option to rotate cards.
     */
    fun startGame(
        players: MutableList<Pair<String, PlayerType>>,
        rotatable: Boolean,
        host : Boolean = false,
        drawPile: MutableList<Card>? = createDrawStack()
    ): MutableList<Card>? {
        if (players.size in 2..6) {
            // get lists of only names and types to create the game
            val playersNames = players.map { it.first }.toMutableList()
            val playersTypes = players.map { it.second }.toMutableList()
            //create the player list
            val playersList = emptyList<Player>().toMutableList()
            val handCards = emptyList<Card>().toMutableList()
            for ((i, _) in players.withIndex()) {
                playersList.add(Player(playersNames[i], handCards, playersTypes[i]))
            }

            // create the gameBoard
            val fieldCards = createGameBoard()
//            val fieldCards = emptyList<MutableList<Card>>().toMutableList()
//            for (x in 0..7) {
//                fieldCards.add(mutableListOf())
//                for (y in 0..7) {
//                    when (y + x * 8) {
//                        27 -> fieldCards[x].add(Card(-2, CardType.POWER_STATION, HashMap(), 0))
//                        28 -> fieldCards[x].add(Card(-3, CardType.POWER_STATION, HashMap(), 0))
//                        35 -> fieldCards[x].add(Card(-4, CardType.POWER_STATION, HashMap(), 0))
//                        36 -> fieldCards[x].add(Card(-5, CardType.POWER_STATION, HashMap(), 0))
//                        else -> fieldCards[x].add(Card(-1, CardType.EMPTY, HashMap(), 0))
//                    }
//                }
//            }

            val lastTurns = Stack<GameTurn>()
            val futureTurns = Stack<GameTurn>()
            val trains = MutableList(32) { Pair(7, false) }
            mapOfMaps[players.size]!!.forEach { it1 -> it1.value.forEach { trains[it - 1] = Pair(it1.key, false) } }
            val field = GameBoard(fieldCards, lastTurns, futureTurns, trains)
            // create the card stack
            val cardsToSend = drawPile?.toMutableList()
            rootService.mainGame = MainGame(drawPile!!, playersList, field, rotatable)
            dealCards()
            if (!host) {
                onAllRefreshables { refreshAfterStartGame() }
            }
            return cardsToSend
        }
        else{
            rootService.mainGame = null
            return null
        }
    }

    private fun createGameBoard(): MutableList<MutableList<Card>> {
        val fieldCards = emptyList<MutableList<Card>>().toMutableList()
        for (x in 0..7) {
            fieldCards.add(mutableListOf())
            for (y in 0..7) {
                when (y + x * 8) {
                    27 -> fieldCards[x].add(Card(-2, CardType.POWER_STATION, HashMap(), 0))
                    28 -> fieldCards[x].add(Card(-3, CardType.POWER_STATION, HashMap(), 0))
                    35 -> fieldCards[x].add(Card(-4, CardType.POWER_STATION, HashMap(), 0))
                    36 -> fieldCards[x].add(Card(-5, CardType.POWER_STATION, HashMap(), 0))
                    else -> fieldCards[x].add(Card(-1, CardType.EMPTY, HashMap(), 0))
                }
            }
        }
        return fieldCards

    }

    /**
     * ends the game
     */
    fun endGame() {
        onAllRefreshables { refreshAfterEndGame() }
    }

    /**
     * Calculates the winner of the game and return a string with his name
     */
    fun computeWinner():  Map<Player, Int> {
        //get the current game
        val game = rootService.mainGame
        checkNotNull(game) { "no game available" }
        val hashMapOfPlayers = HashMap<Player, Int>()

        for (player in game.players) {
            hashMapOfPlayers[player] = player.points
        }
        // sort the HashMap by the values in ascending order and output it
        return hashMapOfPlayers.toList().sortedBy { (_, value) -> value }.toMap()
    }

    /**
     * Returns the coordinates of the free Fields
     */
    fun getFreeFields(state: State? = null): MutableList<Pair<Int, Int>> {
        val mG = rootService.mainGame ?: throw IllegalStateException("No game available")
        val field = state?.field ?: mG.field.fieldCards
        val returnValue = mutableListOf<Pair<Int, Int>>()
        for (i in 0..7) {
            for (j in 0..7) {
                val cond = (i % 7 == 0 || j % 7 == 0 || rootService.legalService.nextToCheck(i, j))
                if (field[i][j].cardType == CardType.EMPTY && cond) {
                    returnValue.add(Pair(i, j))
                }
            }
        }
        return returnValue
    }


    /**
     * Help method to create the draw stack cards before the start of the game
     */

    private fun createDrawStack(): MutableList<Card> {
        val cardsDeck = emptyList<Card>().toMutableList()
        var i = 0
        val stream = GameService::class.java.getResourceAsStream("/tiles.csv")
        stream.bufferedReader().use(BufferedReader::readText).lines().forEach{
            val map = hashMapOf(
                it[1].digitToInt() to it[3].digitToInt(),
                it[3].digitToInt() to it[1].digitToInt(),
                it[7].digitToInt() to it[9].digitToInt(),
                it[9].digitToInt() to it[7].digitToInt(),
                it[13].digitToInt() to it[15].digitToInt(),
                it[15].digitToInt() to it[13].digitToInt(),
                it[19].digitToInt() to it[21].digitToInt(),
                it[21].digitToInt() to it[19].digitToInt()
            )
            cardsDeck.add(Card(i,CardType.TRAFFIC,map,0))
            i++
        }
        cardsDeck.shuffle()
        return cardsDeck
    }


    /**
     * creates a usable draw pile for a game by inverting all incoming and outgoing connections from a card, creating  a
     * new card with this information and adding it to the draw pile
     * @param tiles: a draw pile with (half of the) connections
     */
    fun createDrawPileFromTileConnections(tiles: MutableList<Card>): MutableList<Card>{
        val cardDeck = emptyList<Card>().toMutableList()

        for( tile  in tiles){
            val routes = hashMapOf<Int, Int>()
            for(route in tile.routes){
                routes[route.key] = route.value
                routes[route.value] = route.key
            }
            cardDeck.add(Card(tile.id ,CardType.TRAFFIC,routes,0))
        }
        return cardDeck
    }


    /**
     * Help method to deal the first cards to the players before the game starts
     */

    private fun dealCards() {
        val game = rootService.mainGame
        checkNotNull(game) { "No game running !" }
        for (player in game.players) {
            player.handCards = mutableListOf(game.drawPile.first())
            game.drawPile.removeFirst()
        }
    }
}