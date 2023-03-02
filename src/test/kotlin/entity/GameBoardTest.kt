package entity
import java.util.Stack
import kotlin.test.*

/**
 * class to test Game Board Class
 */
class GameBoardTest {
    /**
     * variables to help with testing
     */
    private val pts1 = mutableListOf(5,5,6,7)
    private val trains1 = mutableListOf(1,2,3,4)
    private val pts2 = mutableListOf(5,8,60,70)
    private val trains2 = mutableListOf(1,2,3,4)
    private val pts3 = mutableListOf(50,80,60,70)
    private val trains3 = mutableListOf(1,2,3,4)
    private val turn1 = GameTurn(false, 90, 5, 7, pts1, trains1)
    private val turn2 = GameTurn(true, 0, 2, 8, pts2, trains2)
    private val turn3 = GameTurn(true, 90, 3, 8, pts3, trains3)
    private val lastTurns = Stack<GameTurn>()
    private val futureTurns = Stack<GameTurn>()
    private val trains = mutableListOf(
        Pair(1,true),
        Pair(2,true),
        Pair(3,false),
        Pair(4,false)
    )
    private val routes = hashMapOf(Pair(1,1))
    private val card1 = Card(1,CardType.POWER_STATION, routes ,90)
    private val card2 = Card(2,CardType.EMPTY, routes ,0)
    private val fieldCards = mutableListOf(mutableListOf(card1,card2))

    /**
     * testing method
     */
    @Test
    fun test(){
        lastTurns.add(turn1)
        lastTurns.add(turn2)
        lastTurns.add(turn3)
        futureTurns.add(turn1)
        futureTurns.add(turn2)

        val gameBoard = GameBoard(fieldCards,lastTurns,futureTurns,trains)
        gameBoard.futureTurns.add(turn3)
        assertNotEquals(gameBoard.fieldCards[0][1], card1)
        assertEquals(gameBoard.lastTurns[0] , turn1)
        assertEquals(gameBoard.futureTurns[0] , turn1)
        assertEquals(gameBoard.trains[0].first , 1)

    }

}