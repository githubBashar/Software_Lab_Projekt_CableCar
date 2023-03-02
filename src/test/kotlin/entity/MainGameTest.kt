package entity

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

/**
 * test class for [MainGame]
 */
class MainGameTest {

    //instantiating necessary values to create list of cards for draw pile
    private val dpRoute1 = hashMapOf(Pair(0, 1), Pair(2, 7), Pair(3, 4), Pair(5, 6))
    private val dpRoute2 = hashMapOf(Pair(0, 7), Pair(1, 6), Pair(2, 5), Pair(3, 4))
    private val dpCard1 = Card(1, CardType.TRAFFIC, dpRoute1, 0)
    private val dpCard2 = Card(2, CardType.TRAFFIC, dpRoute2, 0)
    private val drawPile = mutableListOf(dpCard1, dpCard2)
    private val drawPile2 = mutableListOf(dpCard2, dpCard1)

    //instantiating necessary values to create players
    private val p1Route = hashMapOf(Pair(0, 5), Pair(1, 2), Pair(3, 6), Pair(4, 7))
    private val p2Route = hashMapOf(Pair(0, 7), Pair(1, 6), Pair(2, 5), Pair(3, 4))
    private val p1Card = Card(3, CardType.TRAFFIC, p1Route, 0)
    private val p2Card = Card(4, CardType.TRAFFIC, p2Route, 0)
    private val p1Cards = mutableListOf(p1Card)
    private val p2Cards = mutableListOf(p2Card)
    private val player1 = Player("Viktor", p1Cards, PlayerType.HUMAN)
    private val player2 = Player("Markus", p2Cards, PlayerType.HARDAI)
    private val players = mutableListOf(player1, player2)

    //instantiating necessary values to create GameBoard
    private val trains = mutableListOf(Pair(1, false), Pair(1, true), Pair(2, false), Pair(2, false))
    private val gbRoutes = hashMapOf(Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0))
    private val gbCard1 = Card(0, CardType.EMPTY, gbRoutes, 0)
    private val gbCard2 = Card(0, CardType.EMPTY, gbRoutes, 0)
    private val fieldCards = mutableListOf(mutableListOf(gbCard1, gbCard2))
    private val gameTurn1 = GameTurn(
        true,
        0,
        1,
        2,
        mutableListOf(1, 2),
        mutableListOf(1, 2, 6)
    )
    private val gameTurn2 = GameTurn(
        true,
        0,
        4,
        7,
        mutableListOf(3, 4),
        mutableListOf(4)
    )
    private val lastTurns = Stack<GameTurn>()
    private val futureTurns = Stack<GameTurn>()
    private lateinit var mainGame: MainGame
    private lateinit var gameBoard: GameBoard

    /**
     * before testing
     */
    @BeforeTest
    fun init() {
        gameBoard = GameBoard(fieldCards, lastTurns, futureTurns, trains)
        mainGame = MainGame(drawPile, players, gameBoard, false)
    }

    /**
     * testing method
     */
    @Test
    fun test() {
        lastTurns.add(gameTurn1)
        futureTurns.add(gameTurn2)
        assertNotNull(mainGame)
        assertNotEquals(mainGame.drawPile[0], mainGame.drawPile[1])
        assertNotEquals(mainGame.players[0], mainGame.players[1])
        assertNotNull(mainGame.field)
        assertEquals(false, mainGame.rotatable)
    }

    /**
     * get current player
     */
    @Test
    fun getCurrentPlayer() {
        assertEquals(mainGame.currentPlayer, 0)
    }

    /**
     * get simulationVit
     */
    @Test
    fun getSimulationVit() {
        assertEquals(mainGame.simulationVit, 1.0)
    }

    /**
     * set current player
     */
    @Test
    fun setCurrentPlayer() {
        mainGame.currentPlayer = 1
        assertEquals(mainGame.currentPlayer, 1)
    }

    /**
     * set drawPile
     */
    @Test
    fun setDrawPile() {
        mainGame.drawPile = drawPile2
        assertEquals(mainGame.drawPile, drawPile2)
    }

    /**
     * set simulationVit
     */
    @Test
    fun setSimulationVit() {
        mainGame.simulationVit = 1.2
        assertEquals(mainGame.simulationVit, 1.2)
    }
}