package entity

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.HashMap
import kotlin.test.BeforeTest
import kotlin.test.assertFails

/**
 * Test cases for the [Player] class
 */
internal class PlayerTest {
    private lateinit var player : Player
    private lateinit var player1 : Player

    /**
     * before test
     */
    @BeforeTest
    fun init() {
        player = Player("test", mutableListOf(Card(2, CardType.TRAFFIC,HashMap<Int,Int>(),90)),PlayerType.HUMAN)

    }

    /**
     *  test player
     */
    @Test
    fun testPlayer(){
        assertFails { player1 = Player("", mutableListOf(Card(2, CardType.TRAFFIC,HashMap<Int,Int>(),90)),PlayerType.HUMAN) }

    }

    /**
     * get points
     */
    @Test
    fun getPoints() {
        assertEquals(player.points,0)
    }

    /**
     * set points
     */
    @Test
    fun setPoints() {
        player.points=3
        assertEquals(player.points,3)
    }

    /**
     * test if the toString function works
     */
    @Test
    fun testToString() {
        assertEquals(player.toString(),"test: 0")
    }

    /**
     * get name
     */
    @Test
    fun getName() {
        assertEquals(player.name,"test")
    }

    /**
     * get hand cards
     */
    @Test
    fun getHandCards() {
        assertEquals(player.handCards,listOf(Card(2, CardType.TRAFFIC,HashMap<Int,Int>(),90)))
    }

    /**
     * set hand cards
     */
    @Test
    fun setHandCards() {
        player.handCards= mutableListOf(Card(0, CardType.POWER_STATION,HashMap<Int,Int>(),90))
        assertEquals(player.handCards,listOf(Card(0, CardType.POWER_STATION,HashMap<Int,Int>(),90)))
    }

    /**
     * get player type
     */
    @Test
    fun getPlayerType() {
        assertEquals(player.playerType,PlayerType.HUMAN)
    }
}