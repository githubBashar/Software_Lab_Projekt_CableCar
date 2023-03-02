package entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

/**
 * Test cases for the [GameTurn] class
 */
internal class GameTurnTest {
    private lateinit var gameTurn : GameTurn
    /**
     * before testing
     */
    @BeforeTest
    fun init() {
        gameTurn = GameTurn(true,90,20,15, listOf(3,5).toMutableList(),
            listOf(10,20,30).toMutableList())
    }

    /**
     * test if it is not the second card
     */
    @Test
    fun isHandCard() {
        assertTrue(gameTurn.isHandCard)
    }

    /**
     * get angle
     */
    @Test
    fun getAngle() {
        assertEquals(gameTurn.angle,90)
    }

    /**
     * get posX
     */
    @Test
    fun getPosX() {
        assertEquals(gameTurn.posX,20)
    }

    /**
     * get posY
     */
    @Test
    fun getPosY() {
        assertEquals(gameTurn.posY,15)
    }

    /**
     * get pointsDifferences
     */
    @Test
    fun getPointsDifferences() {
        assertEquals(gameTurn.pointsDifferences, listOf(3,5))
    }

    /**
     * get trains
     */
    @Test
    fun getTrains() {
        assertEquals(gameTurn.trains,listOf(10,20,30))
    }
}