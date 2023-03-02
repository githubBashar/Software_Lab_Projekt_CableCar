package entity

import java.lang.IllegalStateException
import kotlin.test.*

/**
 * Test cases for the [Card] class
 */
class CardTest {

    /**
     * Construction Testing
     */
    @Test
    fun testConstruction() {
       Card(4, CardType.TRAFFIC, HashMap(), 0)
    }

    /**
     * Test if the getWay function works
     */
    @Test
    fun testGetWay() {
        val cardOne = Card(4, CardType.TRAFFIC, HashMap(), 0)
        val cardTwo = Card(5, CardType.TRAFFIC, HashMap(), 90)
        val cardThree = Card(6, CardType.TRAFFIC, HashMap(), 180)
        val cardFour = Card(7, CardType.TRAFFIC, HashMap(), 270)
        val cardFive = Card(8, CardType.TRAFFIC, HashMap(), 849)

        cardOne.routes[7] = 7
        var d = cardOne.getWay(7)
        assertEquals(2, d)

        cardTwo.routes[6] = 0
        cardTwo.routes[5] = 7
        d = cardTwo.getWay(0)
        assertEquals(7, d)
        d = cardTwo.getWay(7)
        assertEquals(4, d)

        cardThree.routes[7] = 0
        cardThree.routes[0] = 7
        d = cardThree.getWay(3)
        assertEquals(1, d)
        d = cardThree.getWay(4)
        assertEquals(6, d)

        cardFour.routes[7] = 2
        cardFour.routes[0] = 7
        d = cardFour.getWay(5)
        assertEquals(5, d)
        d = cardFour.getWay(6)
        assertEquals(0, d)

        assertFailsWith<IllegalStateException> { cardFive.getWay(2) }


    }

}