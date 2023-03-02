package service

import edu.udo.cs.sopra.ntf.TurnMessage
import edu.udo.cs.sopra.ntf.GameInitMessage
import edu.udo.cs.sopra.ntf.PlayerInfo
import edu.udo.cs.sopra.ntf.Tile
import entity.Card
import entity.CardType
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.net.client.BoardGameClient
import tools.aqua.bgw.net.client.NetworkLogging
import tools.aqua.bgw.net.common.annotations.GameActionReceiver
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.CreateGameResponseStatus
import tools.aqua.bgw.net.common.response.JoinGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponseStatus
import tools.aqua.bgw.net.common.notification.PlayerLeftNotification
import tools.aqua.bgw.net.common.response.*

/**
 * The client service handle messages and responses sent by the server
 * @param playerName: name of the player
 * @param host: Server address
 * @param secret: a secret
 * @param networkService: Service for the host and guest actions
 */
class NetworkClientService(playerName: String, host: String,secret: String, var networkService: NetworkService,
                           var isHardAI : Boolean = false, var isEasyAI : Boolean = false
): BoardGameClient(playerName, host, secret, NetworkLogging.VERBOSE) {

    var sessionID: String? = null // the identifier of this game session; can be null if no session started yet.

    /**
     * Handle a [JoinGameResponse] sent by the server. Guest ill await the init message when its
     * status is [JoinGameResponseStatus.SUCCESS]. The method disconnects from the server and throws an
     * [IllegalStateException] otherwise.
     *
     * @throws IllegalStateException if status != success or currently not waiting for a join game response.
     */
    override fun onJoinGameResponse(response: JoinGameResponse) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_JOIN_CONFIRMATION)
            { "unexpected JoinGameResponse" }

            when (response.status) {
                JoinGameResponseStatus.SUCCESS -> {
                    sessionID = response.sessionID
                    networkService.updateConnectionState(ConnectionState.WAITING_FOR_INIT)
                    networkService.onAllRefreshables { refreshAfterJoinGame(response.opponents) }
                }

                else -> {
                    networkService.client = null
                    networkService.onAllRefreshables {
                        refreshAfterError(
                            "Invalid Session ID or Name.:" +
                                    "Network Session Error:" +
                                    "Either there is no active Game with this Session ID or this name is already taken."
                        )
                    }
                    networkService.connectionState = ConnectionState.DISCONNECTED
                    disconnect()
                }
            }
        }
    }


    /**
     * Handle a [PlayerJoinedNotification] sent by the server. If the player list is empty, this
     * method adds itself and the joined player to the list. If it is not empty, the new player is added.
     * @param notification: a message sent by the server and containing information about the newly joined player
     * @throws IllegalStateException if not currently expecting any guests to join.
     *
     */
    override fun onPlayerJoined(notification: PlayerJoinedNotification) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_PLAYERS
                     || networkService.connectionState == ConnectionState.READY_FOR_GAME
                     || networkService.connectionState == ConnectionState.WAITING_FOR_INIT) {"not awaiting any guests."}

            if (networkService.players == null) {
                networkService.players = mutableListOf(
                    Pair(playerName, entity.PlayerType.HUMAN),
                    Pair(notification.sender, entity.PlayerType.HUMAN))

            } else {  networkService.players!!.add(Pair(notification.sender, entity.PlayerType.HUMAN)) }

            networkService.updateConnectionState(ConnectionState.READY_FOR_GAME)
            networkService.onAllRefreshables { refreshAfterPlayerJoined(notification.sender) }
        }
    }


    /**
     * handles a [GameInitMessage] sent by the host and forwarded by the server. It contains the information to start
     * a new game. It extracts the information (players, cards, rotationAllowed) and starts a new game with this
     * information. If the player is the first player, the connection status is updated to PLAYING_TURN, if not,
     * it is updated to WAITING_FOR_TURN.
     * @param message: Message with all the necessary information to start a new game
     * @param sender: Name of the host
     */
    @Suppress("UNUSED_PARAMETER", "UNUSED")
    @GameActionReceiver
    fun onInitGameReceived(message: GameInitMessage, sender: String) {
        BoardGameApplication.runOnGUIThread {
            val playerInfos = getPlayers(message.players)
            val rotationAllowed = message.rotationAllowed
            val networkTiles = getCardsForLocalGame(message.tileSupply)
            val drawPile = networkService.rootService.gameService.createDrawPileFromTileConnections(networkTiles)


            val firstPlayer = playerInfos[0].first
            if(firstPlayer == playerName) { networkService.updateConnectionState(ConnectionState.PLAYING_TURN) }
            else { networkService.updateConnectionState(ConnectionState.WAITING_FOR_TURN) }

            networkService.players = playerInfos
            networkService.rootService.gameService.startGame(
                players = playerInfos,
                rotatable = rotationAllowed,
                true,
                drawPile = drawPile,

            )

            // ConnectionState should be sat before startGame()
            /*val firstPlayer = rootService.mainGame!!.players[rootService.mainGame!!.currentPlayer].name
            if(firstPlayer == playerName) { networkService.updateConnectionState(ConnectionState.PLAYING_TURN) }
            else { networkService.updateConnectionState(ConnectionState.WAITING_FOR_TURN) }*/
            networkService.onAllRefreshables { refreshAfterStartGame() }

        }
    }


    /**
     * handles a [CreateGameResponse] sent by the Server. The response status should be "success". If the opening of
     * the game was successful, the host is ready to let the guests in.
     * @param response: contains the information, whether a new game successfully was opened or not
     */
    override fun onCreateGameResponse(response: CreateGameResponse) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_HOST_CONFIRMATION)
            { "unexpected CreateGameResponse" }
            when (response.status) {
                CreateGameResponseStatus.SUCCESS -> {
                    networkService.updateConnectionState(ConnectionState.WAITING_FOR_PLAYERS)
                    sessionID = response.sessionID
                }
                else -> {
                    disconnect()
                }
            }
        }
    }


    /**
     * wirf aufgerufen, wenn [TurnMessage] übers Netzwerk empfangen wird.
     * @param message eine [TurnMessage].
     * @param playerName Name des Senders.
     */
    @GameActionReceiver
    fun onTurnMessageReceived(message : TurnMessage, playerName: String) {
        check(networkService.connectionState == ConnectionState.PLAYING_TURN ||
                networkService.connectionState == ConnectionState.WAITING_FOR_TURN)
        BoardGameApplication.runOnGUIThread {
            networkService.receiveTurnMessage(message, playerName)
        }
    }


    override fun onPlayerLeft(notification: PlayerLeftNotification) {
        BoardGameApplication.runOnGUIThread {
            if (networkService.connectionState == ConnectionState.READY_FOR_GAME ||
                networkService.connectionState == ConnectionState.WAITING_FOR_PLAYERS ||
                networkService.connectionState == ConnectionState.WAITING_FOR_INIT
            ) {
                networkService.onAllRefreshables { refreshAfterPlayerLeft(notification.sender) }
            } else if ((networkService.connectionState != ConnectionState.WAITING_FOR_PLAYERS
                        || networkService.connectionState != ConnectionState.WAITING_FOR_INIT) &&
                networkService.connectionState != ConnectionState.DISCONNECTED
            ) {
                networkService.onAllRefreshables {
                    refreshAfterError("Someone has left the game: Network Error:The game cannot be continued!" )
                }
            }
        }
    }


    override fun onGameActionResponse(response: GameActionResponse) {
        super.onGameActionResponse(response)
        if (response.status == GameActionResponseStatus.INVALID_JSON
            || response.status == GameActionResponseStatus.SERVER_ERROR
            || response.status == GameActionResponseStatus.NO_ASSOCIATED_GAME
        ) {
            BoardGameApplication.runOnGUIThread {
                //check(networkService.connectionState != ConnectionState.WAITING_FOR_PLAYERS)
                var errors = ""
                for (error in response.errorMessages)
                    errors += "$error "
                networkService.onAllRefreshables { refreshAfterError("${errors}:Network Error:Network Error") }
            }
        }
    }


    private fun getPlayers(playerInfos: List<PlayerInfo>): MutableList<Pair<String, entity.PlayerType>> {
        val players = mutableListOf<Pair<String, entity.PlayerType>>()
        for(info in playerInfos){
            players.add(Pair(info.name, entity.PlayerType.HUMAN))
        }
        return players
    }


    private fun getCardsForLocalGame(tiles: List<Tile>): MutableList<Card> {
        val cards = mutableListOf<Card>()
        for(tile in tiles){
            val id = tile.id
            val routes = HashMap<Int, Int>()
            for(connectionInfo in tile.connections){ routes[connectionInfo.nodeOne] = connectionInfo.nodeTwo }
            cards.add(Card(id=id, routes=routes, cardType = CardType.TRAFFIC, angle = 0))
        }
        return cards
    }
}
