package service

import entity.*
import java.util.Stack
import kotlin.test.*

/**
 * Test class for [LegalService]
 */
internal class LegalServiceTest {

    //rotatable game
    private val rS1 = RootService()
    private val lS1 = LegalService(rS1)

    //not rotatable game
    private val rS2 = RootService()

    private val card1 = Card(0, cardType = CardType.TRAFFIC,
        hashMapOf(0 to 1, 1 to 0, 2 to 3, 3 to 2, 4 to 5, 5 to 4, 6 to 7, 7 to 6),
        0)
    private val card2 = Card(0, cardType = CardType.TRAFFIC,
        hashMapOf(0 to 5, 5 to 0, 1 to 4, 4 to 1, 2 to 7, 7 to 2, 3 to 6, 6 to 3),
        0)
    private val players = mutableListOf(
        Player("Olaf", mutableListOf(card2), PlayerType.HUMAN),
        Player("Günther", mutableListOf(card2), PlayerType.HUMAN))

    /**
     * Creates a test game for this test class
     */
    @BeforeTest
    fun prepareTestGame(){

        //----------- Prepare gameBoard ------------------------------------------------------
        val cards = mutableListOf<MutableList<Card>>()
        for(i in 0 .. 7){
            cards.add(mutableListOf())
            repeat(8){
                cards[i].add(Card(0, CardType.EMPTY, hashMapOf(), 0))
            }
        }
        val trains = mutableListOf<Pair<Int, Boolean>>()
        for(i in 0..31){
            trains.add(Pair(1, false))
        }
        cards[4][4] = card1
        val gameBoard = GameBoard(cards, Stack(), Stack(), trains)
        //------------------------------------------------------------------------------------
        rS1.mainGame = MainGame(mutableListOf(), players, gameBoard, true)
        rS2.mainGame = MainGame(mutableListOf(), players, gameBoard, false)
    }

    /**
     * Test errorMemory
     */
    @Test
    fun testErrorMemory() {
        assertEquals("", lS1.errorMemory)
        lS1.errorMemory = "test"
        assertEquals("test", lS1.errorMemory)
    }

    /**
     * Test the checkLegality() function
     */
    @Test
    fun checkLegality() {
        val mG = rS1.mainGame
        checkNotNull(mG)

        //Error-Test
        assertFailsWith<IllegalArgumentException> {lS1.checkLegality(-1,4)}
        assertFailsWith<IllegalArgumentException> {lS1.checkLegality(8,1)}
        assertFailsWith<IllegalArgumentException> {lS1.checkLegality(1,-1)}
        assertFailsWith<IllegalArgumentException> {lS1.checkLegality(1,9)}

        //Put the card on a not empty place
        mG.field.fieldCards[0][0] = card2
        assertFalse(lS1.checkLegality(0,0))

        //Put the card at a free field at the border
        assertTrue(lS1.checkLegality(1,0))

        //Put the card next to another card
        assertTrue(lS1.checkLegality(3,4))
        assertTrue(lS1.checkLegality(5,4))
        assertTrue(lS1.checkLegality(4,3))
        assertTrue(lS1.checkLegality(4,5))

        //Put the card on a place where there´s no other card beside
        assertFalse(lS1.checkLegality(6,6))

        //Create a one point way
        mG.players[mG.currentPlayer].handCards = mutableListOf(card1)
        assertFalse { lS1.checkLegality(3,0) }

        //TODO(" Test case for anyPossibleWay ")

    }

    /**
     * Test the nextToCheck() function
     */
    @Test
    fun nextToCheck() {
        //Error-Test
        assertFailsWith<IllegalArgumentException> {lS1.nextToCheck(-1,4)}
        assertFailsWith<IllegalArgumentException> {lS1.nextToCheck(8,4)}
        assertFailsWith<IllegalArgumentException> {lS1.nextToCheck(1,-1)}
        assertFailsWith<IllegalArgumentException> {lS1.nextToCheck(1,8)}

        //Assert true with all four possible ways
        assertTrue(lS1.nextToCheck(3,4))
        assertTrue(lS1.nextToCheck(5,4))
        assertTrue(lS1.nextToCheck(4,3))
        assertTrue(lS1.nextToCheck(4,5))

        //Assert false with any random position
        assertFalse(lS1.nextToCheck(6,6))
    }
}