package service
import entity.*
import kotlin.collections.HashMap

import kotlin.test.*

/**
 * Test cases for the [PlayerService] class
 */
class PlayerServiceTest {

    /**
     * Helpful variables
     */
    private var rootService= RootService()

    private val cardZero= Card(4, CardType.TRAFFIC, HashMap() , 0)
    private val cardOne = Card(3, CardType.TRAFFIC, HashMap(), 180)
    private val cardTwo = Card(5, CardType.TRAFFIC, HashMap(), 90)

    private val handCard0        = mutableListOf(cardZero)
    private val handCard0TwoCards= mutableListOf(cardOne,cardTwo)

    private val playerList: MutableList<Pair<String, PlayerType>> =
        mutableListOf(
            Pair("Yusuf", PlayerType.HUMAN), Pair("Sara", PlayerType.HUMAN),
            Pair("laila", PlayerType.HUMAN), Pair("Faruk", PlayerType.HUMAN)
        )

    private val tR = TestRefreshable()

    /**
     * add refreshable tR "testRefreshable"
     */
    @BeforeTest
    fun init() {
        rootService.addRefreshable(tR)
    }
    /**
     * set second card
     */
    @Test
    fun setSecondCard(){
        rootService.playerService.secondCard=false
        assertEquals(false,rootService.playerService.secondCard)
    }

    /**
     * Test if the drawNewCard function works
     */
    @Test
    fun testDrawNewCard(){
        assertFalse(tR.refreshAfterDraw)

        rootService.gameService.startGame(playerList,false)
        val game = rootService.mainGame
        requireNotNull(game)
        val drawPile0=  game.drawPile[0]

        //if the player has only one card and draws one
        game.players[0].handCards=handCard0
        rootService.playerService.drawNewCard()
        assertTrue(tR.refreshAfterDraw)
        tR.reset()

        assertEquals(true, game.players[0].handCards.contains(drawPile0))
        assertEquals(game.players[game.currentPlayer].handCards.size == 2,rootService.playerService.secondCard)
        //the first player now has two cards, and wants to draw again.
        assertFailsWith<IllegalStateException> { rootService.playerService.drawNewCard() }

        //if the drawPile is empty
        val drawPileEmpty= emptyList<Card>().toMutableList()
        game.drawPile = drawPileEmpty
        assertFailsWith<IllegalStateException> {rootService.playerService.drawNewCard() }
    }

    /**
     * Test if the placeCard function works
     */
    @Test
    fun testPlaceCard(){
        rootService.gameService.startGame(playerList,true)
        val game = rootService.mainGame
        requireNotNull(game)

        //be posX=0 , posY=2 where we place the card
        //Check if the place is empty
        assertEquals(Card(-1, CardType.EMPTY, hashMapOf(), 0),game.field.fieldCards[2][2])

        game.drawPile[4]=Card(53,CardType.TRAFFIC,
                        hashMapOf(0 to 5, 1 to 6, 2 to 7, 3 to 4, 4 to 3, 5 to 0, 6 to 1, 7 to 2), 0)
        game.players[game.currentPlayer].handCards= mutableListOf( game.drawPile[4])
        rootService.playerService.placeCard(0,2)
        //This place is not empty
        assertFailsWith<IllegalArgumentException> {  rootService.playerService.placeCard(0,2) }
        //No Traffic beside the Card
        assertFailsWith<IllegalArgumentException> {  rootService.playerService.placeCard(1,6) }
    }

    /**
     * Test if the rotate function works
     */
    @Test
    fun testNotRotate(){
        rootService.gameService.startGame(playerList,false)
        val game = rootService.mainGame
        requireNotNull(game)
        assertEquals(false,game.rotatable)
        game.players[0].handCards=handCard0
        assertFailsWith<IllegalStateException> { rootService.playerService.rotate() }
    }

    /**
     * Test if the rotate function works,
     * if the player has one card
     */
    @Test
    fun testRotateOneCard(){
        rootService.gameService.startGame(playerList,true)
        val game = rootService.mainGame
        requireNotNull(game)
        assertEquals(true,game.rotatable)
        game.players[0].handCards=handCard0
        rootService.playerService.rotate()
        assertEquals(Card(4, CardType.TRAFFIC, HashMap(), 90),game.players[0].handCards[0])
    }

    /**
     * Test if the rotate function works,
     * if the player has two cards
     */
    @Test
    fun testRotateTwoCards(){
        rootService.gameService.startGame(playerList,true)
        val game = rootService.mainGame
        requireNotNull(game)
        assertEquals(true,game.rotatable)

        //if there are two cards, then rotate only the second
        game.players[0].handCards=handCard0TwoCards
        rootService.playerService.rotate()
        assertEquals(Card(3, CardType.TRAFFIC, HashMap(), 180),game.players[0].handCards[0])
        assertEquals(Card(5, CardType.TRAFFIC, HashMap(), 180),game.players[0].handCards[1])
    }

    /**
     * Test if the nextPlayer function works
     */
    @Test
    fun testNextPlayer(){
          assertFalse(tR.refreshAfterTurnEnds)
          assertFalse(tR.refreshAfterEndGame)

        rootService.gameService.startGame(playerList,false)
        val game = rootService.mainGame
        requireNotNull(game)
        assertEquals(false,rootService.playerService.secondCard)

        assertEquals(game.currentPlayer, 0)
        rootService.playerService.nextPlayer()
        assertTrue(tR.refreshAfterTurnEnds)
        tR.reset()
        // following players are AI so currPlayer will be updates two times inside nextPlayer()
        assertEquals(game.currentPlayer, 1)
        rootService.playerService.nextPlayer()
        assertTrue(tR.refreshAfterTurnEnds)
        tR.reset()
        assertEquals(game.currentPlayer, 2)
        rootService.playerService.nextPlayer()
        assertTrue(tR.refreshAfterTurnEnds)
        tR.reset()
        assertEquals(game.currentPlayer, 3)

       val card = Card(7, CardType.TRAFFIC, HashMap(), 90)

        for(i in  0..7){
            for(j in 0..7){
                game.field.fieldCards[i][j]=card
            }
        }
        rootService.playerService.nextPlayer()
        assertTrue(tR.refreshAfterEndGame)
        tR.reset()

    }
}
