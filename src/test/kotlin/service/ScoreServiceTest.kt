package service

import entity.Card
import entity.CardType
import entity.GameBoard
import entity.MainGame
import kotlin.test.*
import java.util.Stack

/**
 * The test class for [ScoreService]
 */
internal class ScoreServiceTest {

    private val rS = RootService()
    private val sS = ScoreService(rS)

    private val cards = mutableListOf(
        //U-Turn
        Card(0, cardType = CardType.TRAFFIC,
            hashMapOf(0 to 1, 1 to 0, 2 to 3, 3 to 2, 4 to 5, 5 to 4, 6 to 7, 7 to 6),
        0),
        //Straight forward
        Card(0, cardType = CardType.TRAFFIC,
            hashMapOf(0 to 5, 5 to 0, 1 to 4, 4 to 1, 2 to 7, 7 to 2, 6 to 3, 3 to 6),
            0),
        //0 and 1 to the left, 4 and 5 to the left
        Card(0, cardType = CardType.TRAFFIC,
            hashMapOf(0 to 3, 3 to 0, 1 to 2, 2 to 1, 4 to 7, 7 to 4, 6 to 5, 5 to 6),
            0),
        //0 and 1 to the right, 4 and 5 to the right
        Card(0, cardType = CardType.TRAFFIC,
            hashMapOf(0 to 7, 7 to 0, 1 to 6, 6 to 1, 2 to 5, 5 to 2, 3 to 4, 4 to 3),
            0),
        //Power station
        Card(0, cardType = CardType.POWER_STATION,
        hashMapOf(0 to 1, 1 to 0, 2 to 3, 3 to 2, 4 to 5, 5 to 4, 6 to 7, 7 to 6),
        0)
    )
    private val gameBoard = GameBoard(mutableListOf(mutableListOf()), Stack(), Stack(), mutableListOf())

    /**
     * Creates test routes on the GameBoard
     */
    @BeforeTest
    fun setUpGameBoard(){
        //Place empty cards
        for(i in 0 ..7){
            gameBoard.fieldCards.add(mutableListOf())
            repeat(8){
                gameBoard.fieldCards[i].add(Card(-1, CardType.EMPTY, hashMapOf(), 0))
            }
        }
        gameBoard.fieldCards.removeLast()

        //Place the trains
        for(i in 0 until 32){
            gameBoard.trains.add(Pair(i%2, false))
        }

        //Place test routes

        //8 Points for train 1 and 18 (border)
        for(i in 0 .. 7){
            gameBoard.fieldCards[1][i] = cards[1]
        }

        //15 Points for train 2 (border) and 1 for train 17 (border)
        for(i in 0 .. 6){
            gameBoard.fieldCards[2][i] = cards[1]
        }
        gameBoard.fieldCards[2][7] = cards[0]

        //Double points for train 3: 1*2 = 2 (powerStation)
        gameBoard.fieldCards[3][0] = cards[1]
        gameBoard.fieldCards[3][1] = cards[4]

        //All directions for train 9
        gameBoard.fieldCards[7][1] = cards[1] //left
        gameBoard.fieldCards[6][1] = cards[3] //down
        gameBoard.fieldCards[6][2] = cards[2] //right
        gameBoard.fieldCards[7][2] = cards[3] //top until [7][0] (empty-card) -> no points
    }

    /**
     * Test the method computeScore(): MutableList<Pair<Int, Int>>
     */
    @Test
    fun computeScore() {
        rS.mainGame = MainGame(mutableListOf(), mutableListOf(), gameBoard, false)
        requireNotNull(rS.mainGame)
        val test = sS.computeScore()
        //5 trains should have points
        assertEquals(5, test.size)
        assertEquals(Pair(1,8), test[0])
        assertEquals(Pair(2,15), test[1])
        assertEquals(Pair(3,2), test[2])
        assertEquals(Pair(21,1), test[3])
        assertEquals(Pair(22,8), test[4])

        //TODO("Exceptions test")
    }
}