package service

import entity.Card
import entity.CardType
import entity.GameTurn
import entity.PlayerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.*

/**
 * Undo service test
 */
internal class UndoServiceTest {
    private val rS = RootService()
    private val gS = rS.gameService
    private val urS = rS.undoRedoService
    private val tR = TestRefreshable()


    /**
     * Init before test
     *
     */
    @BeforeTest
    fun init() {
        rS.addRefreshable(tR)
    }

    /**
     * test 1 : fail call of undo game is null
     * test 2 : empty lastTurns
     */
    @Test
    fun undo() {
        assertFalse(tR.refreshAfterUndo)
        assertFails { urS.undo() }

        gS.startGame(mutableListOf(Pair("player1", PlayerType.HUMAN), Pair("player2", PlayerType.HUMAN)), false)
        rS.mainGame!!.field.fieldCards[3][3] =
            Card(0, CardType.TRAFFIC, hashMapOf(0 to 3, 3 to 0, 1 to 2, 2 to 1, 4 to 7, 7 to 4, 6 to 5, 5 to 6), 0)

        assertFalse(tR.refreshAfterUndo)
        urS.undo()
        assertTrue(tR.refreshAfterUndo)
    }

    /**
     * test : lastTurn not empty case GameTurn with isHandCard = true
     */
    @Test
    fun undo1() {
        gS.startGame(mutableListOf(Pair("player1", PlayerType.HUMAN), Pair("player2", PlayerType.HUMAN)), false)
        rS.mainGame!!.field.fieldCards[3][3] =
            Card(0, CardType.TRAFFIC, hashMapOf(0 to 3, 3 to 0, 1 to 2, 2 to 1, 4 to 7, 7 to 4, 6 to 5, 5 to 6), 0)

        rS.mainGame!!.field.lastTurns.push(GameTurn(true, 0, 3, 3, mutableListOf(2, 3), mutableListOf(2)))
        val playerCard = rS.mainGame!!.players[1].handCards[0]
        rS.mainGame!!.field.trains[2] = Pair(rS.mainGame!!.field.trains[2].first, true)
        assertFalse(tR.refreshAfterUndo)
        urS.undo()
        assertEquals(0, rS.mainGame!!.field.lastTurns.size)
        assertEquals(1, rS.mainGame!!.field.futureTurns.size)
        val points1 = rS.mainGame!!.players[0].points
        val points2 = rS.mainGame!!.players[1].points
        val card =
            Card(0, CardType.TRAFFIC, hashMapOf(0 to 3, 3 to 0, 1 to 2, 2 to 1, 4 to 7, 7 to 4, 6 to 5, 5 to 6), 0)
        assertEquals(rS.mainGame!!.drawPile[0], playerCard)
        assertEquals(rS.mainGame!!.currentPlayer, 1)
        assertEquals(rS.mainGame!!.players[1].handCards, mutableListOf(card))
        assertEquals(rS.mainGame!!.field.fieldCards[3][3], Card(-2, CardType.EMPTY, HashMap(), 0))
        assertEquals(-2, points1)
        assertEquals(-3, points2)
        assertEquals(false, rS.mainGame!!.field.trains[2].second)
        assertTrue(tR.refreshAfterUndo)
    }

    /**
     * test : lastTurn not empty case GameTurn with isHandCard = false
     */
    @Test
    fun undo2() {
        gS.startGame(mutableListOf(Pair("player1", PlayerType.HUMAN), Pair("player2", PlayerType.HUMAN)), false)
        rS.mainGame!!.field.fieldCards[3][3] =
            Card(0, CardType.TRAFFIC, hashMapOf(0 to 3, 3 to 0, 1 to 2, 2 to 1, 4 to 7, 7 to 4, 6 to 5, 5 to 6), 0)
        rS.addRefreshable(tR)

        rS.mainGame!!.field.lastTurns.push(GameTurn(false, 0, 3, 3, mutableListOf(2, 3), mutableListOf(2)))
        val playerCard = rS.mainGame!!.players[1].handCards[0]
        rS.mainGame!!.field.trains[2] = Pair(rS.mainGame!!.field.trains[2].first, true)
        assertFalse(tR.refreshAfterUndo)
        urS.undo()
        assertEquals(0, rS.mainGame!!.field.lastTurns.size)
        assertEquals(1, rS.mainGame!!.field.futureTurns.size)
        val points1 = rS.mainGame!!.players[0].points
        val points2 = rS.mainGame!!.players[1].points
        val card =
            Card(0, CardType.TRAFFIC, hashMapOf(0 to 3, 3 to 0, 1 to 2, 2 to 1, 4 to 7, 7 to 4, 6 to 5, 5 to 6), 0)
        assertEquals(rS.mainGame!!.drawPile[0], card)
        assertEquals(rS.mainGame!!.currentPlayer, 1)
        assertEquals(rS.mainGame!!.players[1].handCards, mutableListOf(playerCard))
        assertEquals(rS.mainGame!!.field.fieldCards[3][3], Card(-2, CardType.EMPTY, HashMap(), 0))
        assertEquals(-2, points1)
        assertEquals(-3, points2)
        assertEquals(false, rS.mainGame!!.field.trains[2].second)
        assertTrue(tR.refreshAfterUndo)
    }

    /**
     * after test Call TestRefreshable reset()
     */
    @AfterTest
    fun resetTestRefreshable() {
        tR.reset()
    }
}