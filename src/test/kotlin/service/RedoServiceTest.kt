package service

import entity.GameTurn
import entity.PlayerType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertFails

/**
 * Redo service test
 *
 */
class RedoServiceTest {
    private val rS = RootService()
    private val gS = rS.gameService
    private val urS = rS.undoRedoService
    private val tR = TestRefreshable()

    /**
     * Init before test
     */
    @BeforeTest
    fun init() {
        rS.addRefreshable(tR)
    }

    /**
     * test 1 : fail call of redo game is null
     * test 2 : empty lastTurns
     */
    @Test
    fun redo() {
        assertFalse(tR.refreshAfterRedo)
        assertFails { urS.redo() }

        gS.startGame(
            mutableListOf(
                Pair("player1", PlayerType.HUMAN),
                Pair("player2", PlayerType.HUMAN),
                Pair("player3", PlayerType.HUMAN)
            ), false
        )
        assertFalse(tR.refreshAfterRedo)

        urS.redo()
        assertTrue(tR.refreshAfterRedo)

    }

    /**
     * test : futureTurns not empty case GameTurn with isHandCard = false
     */
    @Test
    fun redo1() {

        gS.startGame(
            mutableListOf(
                Pair("player1", PlayerType.HUMAN),
                Pair("player2", PlayerType.HUMAN),
                Pair("player3", PlayerType.HUMAN)
            ), false
        )

        rS.mainGame!!.field.futureTurns.push(GameTurn(false, 0, 3, 3, mutableListOf(2, 3, 0), mutableListOf(2)))
        val card = rS.mainGame!!.players[1].handCards[0]
        val cardDraw = rS.mainGame!!.drawPile[0]
        assertFalse(tR.refreshAfterRedo)
        urS.redo()
        assertEquals(1, rS.mainGame!!.field.lastTurns.size)
        assertEquals(0, rS.mainGame!!.field.futureTurns.size)
        assertEquals(1, rS.mainGame!!.currentPlayer)
        assertEquals(2, rS.mainGame!!.players[0].points)
        assertEquals(3, rS.mainGame!!.players[1].points)
        assertEquals(0, rS.mainGame!!.players[2].points)
        assertEquals(cardDraw, rS.mainGame!!.field.fieldCards[3][3])
        assertEquals(card, rS.mainGame!!.players[1].handCards[0])
        assertEquals(true, rS.mainGame!!.field.trains[2].second)
        assertTrue(tR.refreshAfterRedo)
    }

    /**
     * test : futureTurns not empty case GameTurn with isHandCard = true
     */
    @Test
    fun redo2() {
        gS.startGame(
            mutableListOf(
                Pair("player1", PlayerType.HUMAN),
                Pair("player2", PlayerType.HUMAN),
                Pair("player3", PlayerType.HUMAN)
            ), false
        )
        rS.mainGame!!.field.futureTurns.push(GameTurn(true, 0, 3, 3, mutableListOf(2, 3, 0), mutableListOf(2)))
        val card = rS.mainGame!!.players[1].handCards[0]
        val cardDraw = rS.mainGame!!.drawPile[0]
        assertFalse(tR.refreshAfterRedo)
        urS.redo()
        assertEquals(1, rS.mainGame!!.field.lastTurns.size)
        assertEquals(0, rS.mainGame!!.field.futureTurns.size)
        assertEquals(1, rS.mainGame!!.currentPlayer)
        assertEquals(2, rS.mainGame!!.players[0].points)
        assertEquals(3, rS.mainGame!!.players[1].points)
        assertEquals(0, rS.mainGame!!.players[2].points)
        assertEquals(card, rS.mainGame!!.field.fieldCards[3][3])
        assertEquals(cardDraw, rS.mainGame!!.players[1].handCards[0])
        assertEquals(true, rS.mainGame!!.field.trains[2].second)
        assertTrue(tR.refreshAfterRedo)
    }

    /**
     * after test Call TestRefreshable reset()
     */
    @AfterTest
    fun resetTestRefreshable() {
        tR.reset()
    }

}