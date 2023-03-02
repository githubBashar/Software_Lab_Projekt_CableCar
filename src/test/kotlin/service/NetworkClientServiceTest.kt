package service

import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import kotlin.IllegalStateException
import kotlin.test.*
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.CreateGameResponseStatus
import tools.aqua.bgw.net.common.response.JoinGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponseStatus
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


/**
 * Tests all message and response processing in [NetworkServiceTest].
 * */
class NetworkClientServiceTest {

    /**
     * tests whether the client is set correctly
     */
    @Test
    fun testClient() {
        val rootServiceHost = RootService()
        val networkServiceHost = NetworkService(rootServiceHost)
        val secret = "bl"
        val name = "Host"

        assertNull(networkServiceHost.client)
        val newClient = NetworkClientService(
            playerName = name,
            host = NetworkService.SERVER_ADDRESS,
            secret = secret,
            networkService = networkServiceHost
        )
        networkServiceHost.client = newClient
        assertNotNull(networkServiceHost.client)
        val rootServiceHost2 = RootService()
        val networkServiceHost2 = NetworkService(rootServiceHost2)

        networkServiceHost.client!!.networkService = networkServiceHost2
    }


    /**
     * tests whether the [CreateGameResponse] reaches the client
     */
    @Test
    fun testOnCreateGameResponse() {
        val status = CreateGameResponseStatus.SUCCESS
        val secret = "bl"
        val sessionID = Random.nextInt(0, 1000).toString()
        val response = CreateGameResponse(status = status, sessionID = sessionID)

        // test onHostGameResponse fail
        val rootServiceHost = RootService()
        val networkServiceHost = rootServiceHost.networkService
        val name = "Host"

        val newClient = NetworkClientService(
            playerName = name,
            host = NetworkService.SERVER_ADDRESS,
            secret = secret,
            networkService = networkServiceHost
        )
        networkServiceHost.client = newClient

        // test success
        networkServiceHost.connectionState = ConnectionState.WAITING_FOR_HOST_CONFIRMATION
        networkServiceHost.client!!.onCreateGameResponse(response = response)
        assertEquals(ConnectionState.WAITING_FOR_PLAYERS, networkServiceHost.connectionState)
    }


    /**
     * tests whether the expected exceptions are thrown if the connection state is not the expected one and the status
     * of the message is not equal to "success"
     */
    @Test
    fun testOnCreateGameResponseFail() {
        var status = CreateGameResponseStatus.ALREADY_ASSOCIATED_WITH_GAME
        val secret = "bl"
        val sessionID = Random.nextInt(0, 1000).toString()
        var response = CreateGameResponse(status = status, sessionID = sessionID)

        // test onHostGameResponse fail
        val rootServiceHost = RootService()
        val networkServiceHost = NetworkService(rootServiceHost)
        val name = "Host"

        val newClient = NetworkClientService(
            playerName = name,
            host = NetworkService.SERVER_ADDRESS,
            secret = secret,
            networkService = networkServiceHost
        )
        networkServiceHost.client = newClient

        // Test fail due response
        networkServiceHost.connectionState = ConnectionState.WAITING_FOR_HOST_CONFIRMATION
        assertFailsWith<IllegalStateException> { networkServiceHost.client!!.onCreateGameResponse(response = response) }

        //test fail due connection state
        status = CreateGameResponseStatus.SUCCESS
        response = CreateGameResponse(status = status, sessionID = sessionID)
        networkServiceHost.connectionState = ConnectionState.DISCONNECTED
        assertFailsWith<IllegalStateException> { networkServiceHost.client!!.onCreateGameResponse(response = response) }
        assertEquals(ConnectionState.DISCONNECTED, networkServiceHost.connectionState)
    }


    /**
     * tests whether the [JoinGameResponse] reaches the client
     */
    @Test
    fun testOnJoinGameResponse() {
        val secret = "bl"

        val status = JoinGameResponseStatus.SUCCESS
        val sessionID = Random.nextInt(0, 1000).toString()
        val opponents = listOf("1", "2")
        val message = "message"

        val response = JoinGameResponse(
            status = status,
            sessionID = sessionID,
            opponents = opponents,
            message = message
        )
        val rootServiceGuest = RootService()
        val networkServiceGuest = rootServiceGuest.networkService
        val tR = TestRefreshable()
        rootServiceGuest.addRefreshables(tR)
        val guestName = "Guest"
        val newClient = NetworkClientService(
            playerName = guestName,
            host = NetworkService.SERVER_ADDRESS,
            secret = secret,
            networkService = networkServiceGuest
        )
        networkServiceGuest.client = newClient
        networkServiceGuest.connectionState = ConnectionState.WAITING_FOR_JOIN_CONFIRMATION

        assertFalse(tR.refreshAfterJoinGame)
        networkServiceGuest.client!!.onJoinGameResponse(response = response)
        assertTrue(tR.refreshAfterJoinGame)
        tR.reset()

        assertEquals(ConnectionState.WAITING_FOR_INIT, networkServiceGuest.connectionState)
    }


    /**
     * tests whether the expected exceptions are thrown if the connection state is not the expected one and the status
     * of the message is not equal to "success"
     */
    @Test
    fun testOnJoinGameResponseFail() {
        val secret = "bl"

        val status = JoinGameResponseStatus.SERVER_ERROR
        val sessionID = Random.nextInt(0, 1000).toString()
        val opponents = listOf("1", "2")
        val message = "message"

        val response = JoinGameResponse(
            status = status,
            sessionID = sessionID,
            opponents = opponents,
            message = message
        )
        val rootServiceGuest = RootService()
        val networkServiceGuest = rootServiceGuest.networkService
        val guestName = "Guest"
        val newClient = NetworkClientService(
            playerName = guestName,
            host = NetworkService.SERVER_ADDRESS,
            secret = secret,
            networkService = networkServiceGuest
        )
        networkServiceGuest.client = newClient
        val tR = TestRefreshable()
        rootServiceGuest.addRefreshables(tR)
        // test fail due response
        networkServiceGuest.connectionState = ConnectionState.WAITING_FOR_JOIN_CONFIRMATION

        assertFailsWith<IllegalStateException> { networkServiceGuest.client!!.onJoinGameResponse(response = response) }
        assertFalse { tR.refreshAfterJoinGame }
        assertTrue { tR.refreshAfterError }
        tR.reset()

        // test fail due connection state
        networkServiceGuest.connectionState = ConnectionState.CONNECTED
    }


    /**
     * tests whether the GameInitMessage reaches the client
     */
    @Test
    fun testOnInitGameReceived() {
        val sessionID = Random.nextInt(0, 1000).toString()

        val rootServiceHost = RootService()
        val rootServiceGuest = RootService()

        val networkServiceHost = rootServiceHost.networkService
        val networkServiceGuest = rootServiceGuest.networkService

        val hostName = "Host"
        val guestName = "Guest"
        val tR = TestRefreshable()
        rootServiceGuest.addRefreshables(tR)
        networkServiceHost.hostGame(
            secret = NetworkServiceTest.NETWORK_SECRET,
            name = hostName,
            sessionID = sessionID
        )
        Thread.sleep(2000)
        networkServiceGuest.joinGame(
            secret = NetworkServiceTest.NETWORK_SECRET,
            name = guestName,
            sessionID = sessionID
        )
        Thread.sleep(2000)
        networkServiceHost.startNewHostedGame(
            players = networkServiceHost.players!!,
            rotatable = true
        )
        Thread.sleep(2000)

        assertEquals(ConnectionState.PLAYING_TURN, networkServiceHost.connectionState)
        assertEquals(ConnectionState.WAITING_FOR_TURN, networkServiceGuest.connectionState)
        assertTrue { tR.refreshAfterStartGame }
        tR.reset()

        networkServiceHost.disconnect()
        networkServiceGuest.disconnect()
    }


    /**
     * tests whether the expected exceptions are thrown if the connection state is not the expected one and the status
     * of the message is not equal to "success"
     */
    @Test
    fun testOnInitGameReceivedFail() {
        val sessionID = Random.nextInt(0, 1000).toString()

        val rootServiceHost = RootService()
        val rootServiceGuest = RootService()

        val networkServiceHost = rootServiceHost.networkService
        val networkServiceGuest = rootServiceGuest.networkService

        val hostName = "Host"
        val guestName = "Guest"

        val tR = TestRefreshable()
        rootServiceGuest.addRefreshables(tR)

        Thread.sleep(2000)
        networkServiceHost.hostGame(
            secret = NetworkServiceTest.NETWORK_SECRET,
            name = hostName,
            sessionID = sessionID
        )
        Thread.sleep(2000)
        networkServiceGuest.joinGame(
            secret = NetworkServiceTest.NETWORK_SECRET,
            name = guestName,
            sessionID = sessionID
        )
        Thread.sleep(2000)
        val reversedPlayers = mutableListOf(
            networkServiceHost.players!![1],
            networkServiceHost.players!![0]
        )

        networkServiceHost.startNewHostedGame(
            players = reversedPlayers,
            rotatable = true
        )
        Thread.sleep(2000)
        assertTrue { tR.refreshAfterStartGame }
        tR.reset()

        assertEquals(ConnectionState.WAITING_FOR_TURN, networkServiceHost.connectionState)
        assertEquals(ConnectionState.PLAYING_TURN, networkServiceGuest.connectionState)
    }

    /**
     * tests whether the [PlayerJoinedNotification] reaches the client and whether the expected exceptions are thrown
     * if the connection state is not the expected one and the status of the message is not equal to "success"
     */
    @Test
    fun testOnPlayerJoined() {
        val message = "message"
        val sender = "Simba"
        val notification = PlayerJoinedNotification(
            message = message,
            sender = sender
        )

        val rootServiceHost = RootService()
        val networkServiceHost = rootServiceHost.networkService

        val hostName = "Host"
        val secret = "bl"

        val newClient = NetworkClientService(
            playerName = hostName,
            host = NetworkService.SERVER_ADDRESS,
            secret = secret,
            networkService = networkServiceHost
        )
        networkServiceHost.client = newClient

        val tR = TestRefreshable()
        rootServiceHost.addRefreshables(tR)

        networkServiceHost.connectionState = ConnectionState.WAITING_FOR_PLAYERS
        networkServiceHost.client!!.onPlayerJoined(notification)
        assertTrue { tR.refreshAfterPlayerJoined }
        networkServiceHost.connectionState = ConnectionState.READY_FOR_GAME
        networkServiceHost.client!!.onPlayerJoined(notification)
        networkServiceHost.connectionState = ConnectionState.DISCONNECTED
        assertFailsWith<IllegalStateException> { networkServiceHost.client!!.onPlayerJoined(notification) }
        tR.reset()
        assertFalse { tR.refreshAfterPlayerJoined }

    }


    /**
     * Tests the behavior by an onPlayerLeft message
     */
    @Test
    fun testOnPlayerLeft(){
        val sessionID = Random.nextInt(0, 1000).toString()

        val rootServiceHost = RootService()
        val rootServiceGuest = RootService()

        val networkServiceHost = rootServiceHost.networkService
        val networkServiceGuest = rootServiceGuest.networkService

        val hostName = "Host"
        val guestName = "Guest"

        val tR = TestRefreshable()
        rootServiceHost.addRefreshables(tR)

        Thread.sleep(2000)
        networkServiceHost.hostGame(
            secret = NetworkServiceTest.NETWORK_SECRET,
            name = hostName,
            sessionID = sessionID
        )
        Thread.sleep(2000)
        networkServiceGuest.joinGame(
            secret = NetworkServiceTest.NETWORK_SECRET,
            name = guestName,
            sessionID = sessionID
        )
        networkServiceHost.connectionState = ConnectionState.READY_FOR_GAME
        networkServiceGuest.disconnect()
//        assertTrue { tR.refreshAfterPlayerLeft }
//        tR.reset()

        Thread.sleep(2000)
        networkServiceGuest.joinGame(
            secret = NetworkServiceTest.NETWORK_SECRET,
            name = guestName,
            sessionID = sessionID
        )

        networkServiceHost.connectionState = ConnectionState.WAITING_FOR_PLAYERS
        networkServiceGuest.disconnect()
        //        assertTrue { tR.refreshAfterPlayerLeft }
//        tR.reset()

        Thread.sleep(2000)
        networkServiceGuest.joinGame(
            secret = NetworkServiceTest.NETWORK_SECRET,
            name = guestName,
            sessionID = sessionID
        )

        networkServiceHost.connectionState = ConnectionState.WAITING_FOR_INIT
        networkServiceGuest.disconnect()
//        assertTrue { tR.refreshAfterPlayerLeft }
//        tR.reset()

        Thread.sleep(2000)
        networkServiceGuest.joinGame(
            secret = NetworkServiceTest.NETWORK_SECRET,
            name = guestName,
            sessionID = sessionID
        )

        networkServiceHost.connectionState = ConnectionState.CONNECTED
        networkServiceGuest.disconnect()
//        assertFalse { tR.refreshAfterPlayerLeft }
//        assertTrue { tR.refreshAfterError }
//
//        tR.reset()
    }
}

