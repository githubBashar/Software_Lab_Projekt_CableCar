package service
import java.lang.IllegalArgumentException
import kotlin.IllegalStateException
import kotlin.random.Random
import kotlin.test.*


/**
 * This class tests all host and client actions from [NetworkService]
 */
class NetworkServiceTest {
    companion object { const val NETWORK_SECRET = "cable22" }

    /**
     * tests whether the connection works successfully and the expected exceptions are thrown
     */
    @Test
    fun testConnect(){
        val secret = "cable22"

        val rootServiceHost1 = RootService()
        val hostName = "Test Host"
        val networkServiceHost1 = NetworkService(rootServiceHost1)
        // Tests failure due to connectionState
        networkServiceHost1.connectionState = ConnectionState.CONNECTED
        // Tests failure due to client
        assertFailsWith<IllegalArgumentException> { networkServiceHost1.connect(secret=secret, name = hostName)}
        networkServiceHost1.connectionState = ConnectionState.DISCONNECTED
        networkServiceHost1.client = NetworkClientService(
            playerName = hostName,
            host = NetworkService.SERVER_ADDRESS,
            secret = secret,
            networkService = networkServiceHost1
        )
        assertFailsWith<IllegalArgumentException> { networkServiceHost1.connect(secret=secret, name = hostName)}
        networkServiceHost1.disconnect()
        // Tests failure due to secret
        assertFailsWith<IllegalArgumentException> { networkServiceHost1.connect(secret="", name = hostName) }

        networkServiceHost1.connect(secret=secret, name = hostName)
        assertNotNull(networkServiceHost1.client)
    }


    /**
     * Tests whether the host can create a game and the guests can join this game
     */
    @Test
    fun testJoinAndHostGame(){
        val sessionID = Random.nextInt(0, 1000).toString()

        val rootServiceHost = RootService()
        val rootServiceGuest = RootService()

        val networkServiceHost = rootServiceHost.networkService
        val networkServiceGuest = rootServiceGuest.networkService

        assertFails { networkServiceHost.hostGame(secret = NETWORK_SECRET, name = "", sessionID = sessionID) }
        networkServiceHost.disconnect()
        networkServiceHost.hostGame(secret = NETWORK_SECRET, name = "Host", sessionID = sessionID)

        Thread.sleep(2000)
        assertFails { networkServiceGuest.joinGame(secret = NETWORK_SECRET, name = "", sessionID = sessionID) }
        networkServiceGuest.disconnect()
        networkServiceGuest.joinGame(secret = NETWORK_SECRET, name = "Guest", sessionID = sessionID)
    }


    /**
     * Tests whether the expected exceptions are thrown by joining and hosting a game with incorrect parameters and
     * connection states
     */
    @Test
    fun testStartNewHostedGame(){
        val sessionID = Random.nextInt(0, 1000).toString()

        val rootServiceHost = RootService()
        val rootServiceGuest = RootService()

        val networkServiceHost = rootServiceHost.networkService
        val networkServiceGuest = rootServiceGuest.networkService

        networkServiceHost.hostGame(secret = NETWORK_SECRET, name = "Host", sessionID = sessionID)
        Thread.sleep(2000)
        networkServiceGuest.joinGame(secret = NETWORK_SECRET, name = "Guest", sessionID = sessionID)
        Thread.sleep(2000)

        // test the state check
        networkServiceHost.connectionState = ConnectionState.DISCONNECTED
        assertFailsWith<IllegalStateException> { networkServiceHost.startNewHostedGame(
            players = networkServiceHost.players!!,
            rotatable = true) }
        // Test Ahmed: refreshAfterStartGame: False
        // test whether the function works
        networkServiceHost.connectionState = ConnectionState.READY_FOR_GAME
        networkServiceHost.startNewHostedGame(
            players = networkServiceHost.players!!,
            rotatable = true)
        // Test Ahmed: refreshAfterStartGame: True

        Thread.sleep(2000)
    }
    /**
     *  test if the first turn message is correctly received by an opponent.
     */
    @Test
    fun testStartNewHostedGameAndTurnMessage(){
        val sessionID = Random.nextInt(0, 1000).toString()

        val rootServiceHost = RootService()
        val rootServiceGuest = RootService()

        val networkServiceHost = rootServiceHost.networkService
        val networkServiceGuest = rootServiceGuest.networkService

        networkServiceHost.hostGame(secret = NETWORK_SECRET, name = "Host", sessionID = sessionID)
        Thread.sleep(2000)
        networkServiceGuest.joinGame(secret = NETWORK_SECRET, name = "Guest", sessionID = sessionID)
        Thread.sleep(2000)

        networkServiceHost.startNewHostedGame(
            players = networkServiceHost.players!!,
            rotatable = true)
        Thread.sleep(2000)

        val list = networkServiceHost.getTileInfo()
        assertEquals(0, list.size)

        val scores = networkServiceHost.getPlayerScores()
        assertEquals(0, scores[0])
        assertEquals(0, scores[1])

        var i = 0
        var placed = false
        for (j in 0..7) {
            if (networkServiceHost.rootService.legalService.checkLegality(i, j)) {
                networkServiceHost.rootService.playerService.placeCard(i, j)
                networkServiceHost.sendTurnMessage(networkServiceHost.rootService.mainGame!!.field.lastTurns.peek())
                placed = true
            }
            else if (networkServiceHost.rootService.legalService.checkLegality(j, i)) {
                networkServiceHost.rootService.playerService.placeCard(j, i)
                networkServiceHost.sendTurnMessage(networkServiceHost.rootService.mainGame!!.field.lastTurns.peek())
                placed = true
            }
            if (networkServiceHost.rootService.legalService.checkLegality(i, j)
                || networkServiceHost.rootService.legalService.checkLegality(j, i)){
                break
            }
        }

        if (!placed) {
            i = 7
            for (j in 0..7) {
                if (networkServiceHost.rootService.legalService.checkLegality(i, j)) {
                    networkServiceHost.rootService.playerService.placeCard(i, j)
                    networkServiceHost.sendTurnMessage(networkServiceHost.rootService.mainGame!!.field.lastTurns.peek())
                }
                else if (networkServiceHost.rootService.legalService.checkLegality(j, i)) {
                    networkServiceHost.rootService.playerService.placeCard(j, i)
                    networkServiceHost.sendTurnMessage(networkServiceHost.rootService.mainGame!!.field.lastTurns.peek())
                }
                if (networkServiceHost.rootService.legalService.checkLegality(i, j)
                    || networkServiceHost.rootService.legalService.checkLegality(j, i)){
                    break
                }
            }

        }
        Thread.sleep(3000)
        assertEquals(1, networkServiceGuest.rootService.mainGame!!.field.lastTurns.size)
    }


    /**
     * tests whether the disconnection works successfully and the expected exceptions are thrown
     */
    @Test
    fun testDisconnect(){
        val secret = "cable22"
        val rootServiceHost1 = RootService()
        val hostName = "Test Host"
        val networkServiceHost1 = NetworkService(rootServiceHost1)
        networkServiceHost1.connect(secret=secret, name = hostName)
        networkServiceHost1.disconnect()
        assertNull(networkServiceHost1.client)
    }
}
