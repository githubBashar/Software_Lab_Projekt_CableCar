package service

import entity.*
import kotlin.test.*

/**
 * Test cases for the [GameService] class
 */
class GameServiceTest {

    /**
     * Help variables
     */
    private val rootService = RootService()
    private val players : MutableList<Pair<String,PlayerType>> =
        mutableListOf( Pair("Player1", PlayerType.HUMAN) , Pair("Player2", PlayerType.EASYAI),
            Pair("Player3", PlayerType.HARDAI), Pair("Player4", PlayerType.HUMAN) )
    private val players1 = mutableListOf(
        Pair("player1", PlayerType.EASYAI),
    )
    private val players2 = mutableListOf(
        Pair("player1", PlayerType.EASYAI),
        Pair("player2", PlayerType.HUMAN)
    )
    private val players3 = mutableListOf(
        Pair("player1", PlayerType.EASYAI),
        Pair("player2", PlayerType.HUMAN),
        Pair("player3", PlayerType.HARDAI)
    )
    private val players4 = mutableListOf(
        Pair("player1", PlayerType.EASYAI),
        Pair("player2", PlayerType.HUMAN),
        Pair("player3", PlayerType.HARDAI),
        Pair("player4", PlayerType.HUMAN)
    )

    private val testPlayers2 = mutableListOf(
        Pair("player1", PlayerType.EASYAI),
        Pair("player2", PlayerType.HUMAN)
    )
    private val testPlayers3 = mutableListOf(
        Pair("player1", PlayerType.HARDAI),
        Pair("player2", PlayerType.HUMAN),
        Pair("player3", PlayerType.HARDAI)
    )
    private val testPlayers4 = mutableListOf(
        Pair("player1", PlayerType.EASYAI),
        Pair("player2", PlayerType.HARDAI),
        Pair("player3", PlayerType.HARDAI),
        Pair("player4", PlayerType.HUMAN)
    )
    /**
     * Test getFreeFields method
     */
    @Test
    fun testGetFreeFields() {
        rootService.gameService.startGame(players, true)
        val game = rootService.mainGame
        requireNotNull(game)

        val freeFields = rootService.gameService.getFreeFields()
        assertEquals(28, freeFields.size)

        for (field in freeFields) {
            val card = game.field.fieldCards[field.first][field.second]
            assertEquals(CardType.EMPTY, card.cardType)
        }
    }

    /**
     * Test endGame method
     */
    @Test
    fun endGameTest(){
        rootService.gameService.startGame(players,true)
        val game = rootService.mainGame
        rootService.gameService.endGame()
        requireNotNull(game)
        assertNotNull(game)
    }

    /**
     * Test startGame method
     */
    @Test
    fun testStartGame() {
        rootService.gameService.startGame(players,true)
        assertNull(rootService.networkService.client)
        val game = rootService.mainGame
        requireNotNull(game)
        assertNotNull(game)

        // check that the players list was correctly created
        assertEquals(players.size, game.players.size)
        for ((i, player) in game.players.withIndex()) {
                assertEquals(players[i].first, player.name)
                assertEquals(players[i].second, player.playerType)
                assertEquals(0, player.points)
        }

            // check that the game board was correctly created
            assertEquals(8, game.field.fieldCards.size)
            assertEquals(8, game.field.fieldCards[0].size)

            val middleCards = listOf(
                game.field.fieldCards[3][3],
                game.field.fieldCards[3][4],
                game.field.fieldCards[4][3],
                game.field.fieldCards[4][4]
            )
            for (card in middleCards) {
                assertEquals(CardType.POWER_STATION, card.cardType)
            }

            for (x in 0 until 8) {
                for (y in 0 until 8) {
                    val card = game.field.fieldCards[x][y]
                    if (x !in 3..4 || y !in 3..4) {
                        assertEquals(CardType.EMPTY, card.cardType)
                    }
                }
            }
        // test with different number of players
        rootService.gameService.startGame(players1, false)
        assertEquals(null , rootService.mainGame)

        rootService.gameService.startGame(players2, true)
        assertEquals(2, rootService.mainGame!!.players.size)

        rootService.gameService.startGame(players3, true)
        assertEquals(3, rootService.mainGame!!.players.size)

        rootService.gameService.startGame(players4, true)
        assertEquals(4, rootService.mainGame!!.players.size)

        // test with different types of players
        rootService.gameService.startGame(testPlayers2, true)
        assertEquals(PlayerType.EASYAI, rootService.mainGame!!.players[0].playerType)
        assertEquals(PlayerType.HUMAN, rootService.mainGame!!.players[1].playerType)

        rootService.gameService.startGame(testPlayers3, true)
        assertEquals(PlayerType.HUMAN, rootService.mainGame!!.players[1].playerType)

        rootService.gameService.startGame(testPlayers4, true)
        assertEquals(PlayerType.HARDAI, rootService.mainGame!!.players[1].playerType)

    }

    /**
     * Test startGame method
     */
    @Test
    fun testComputeWinner() {
        // Test the case where there is a clear winner
        val players2 = mutableListOf(
            Pair("player1", PlayerType.EASYAI),
            Pair("player2", PlayerType.HUMAN)
        )

        rootService.gameService.startGame(players2, true)
        checkNotNull(rootService.mainGame)
        rootService.mainGame!!.players[0].points = 50
        rootService.mainGame!!.players[1].points = 40
        val winner = rootService.gameService.computeWinner()
        val maxPoints = winner.values.maxOrNull()
        assertTrue(winner.containsValue(maxPoints))
        assertEquals(50 , maxPoints)

        val players3 = mutableListOf(
            Pair("player1", PlayerType.EASYAI),
            Pair("player2", PlayerType.HUMAN),
            Pair("player3", PlayerType.HARDAI)
        )

        rootService.gameService.startGame(players3, true)
        checkNotNull(rootService.mainGame)
        rootService.mainGame!!.players[0].points = 50
        rootService.mainGame!!.players[1].points = 40
        rootService.mainGame!!.players[2].points = 80
        val winner2 = rootService.gameService.computeWinner()
        val maxPoints2 = winner2.values.maxOrNull()
        assertTrue(winner2.containsValue(maxPoints2))
        assertEquals(80 , maxPoints2)
    }


    }