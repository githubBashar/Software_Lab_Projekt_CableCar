package service

import edu.udo.cs.sopra.ntf.*
import entity.Player
import edu.udo.cs.sopra.ntf.GameInitMessage
import edu.udo.cs.sopra.ntf.TurnMessage
import entity.Card
import entity.CardType
import entity.GameTurn


/**
 * The network service manages all host and client actions
 * @param rootService The connection to the  [RootService]
 */
class NetworkService (val rootService: RootService): AbstractRefreshingService(){
    companion object {
        /** URL of the BGW net server hosted for SoPra participants */
        const val SERVER_ADDRESS = "sopra.cs.tu-dortmund.de:80/bgw-net/connect"
        const val GAME_ID = "CableCar" //Name of the game as registered with the server
    }
    var client: NetworkClientService? = null //Network client. Nullable for offline games.
    var players: MutableList<Pair<String, entity.PlayerType>>? = null

    /**
     * current state of the connection in a network game.
     */
    var connectionState: ConnectionState = ConnectionState.DISCONNECTED


    /**
     * connects the client to the server. If the client is not connected, it returns false.
     * @param secret: secret of the game
     * @param name: name of this client
     */
    fun connect(secret: String, name: String): Boolean {

        require(connectionState == ConnectionState.DISCONNECTED && client == null)
        { "already connected to another game" }
        require(secret.isNotBlank()) { "server secret must be given" }
        require(name.isNotBlank()) { "player name must be given" }
        val newClient =
            NetworkClientService(
                playerName = name,
                host = SERVER_ADDRESS,
                secret = secret,
                networkService = this
            )
        return if (newClient.connect()) {
            this.client = newClient
            updateConnectionState(ConnectionState.CONNECTED)
            true
        } else {
            false
        }
    }

    /**
     * Disconnects the [client] from the server, nulls it and updates the
     * [connectionState] to [ConnectionState.DISCONNECTED]. Can safely be called
     * even if no connection is currently active.
     */
    fun disconnect() {
        client?.apply {
            if (sessionID != null) leaveGame("Goodbye!")
            if (isOpen) disconnect()
        }
        client = null
        updateConnectionState(ConnectionState.DISCONNECTED)
    }

    /**
     * receive the turn made by other players and re-play that turn to synchronize games
     * @param message A message that contains all information about the last turn of another player.
     * @param playerName The name of the player who played the last turn.
     */
    fun receiveTurnMessage(message: TurnMessage, playerName : String) {
        if (rootService.mainGame!!.players[rootService.mainGame!!.currentPlayer].name == playerName) {

            val x = message.posX
            val y = message.posY
            val fromSupply = message.fromSupply
            val rotation = message.rotation
            val verInfo = message.gameStateVerificationInfo
            val placed = verInfo.placedTiles
            var pTile : TileInfo? = null


            for (tile in placed) {
                if (tile.x == x && tile.y == y) {
                    pTile = tile
                    break
                }
            }

            if (pTile == null)
                onAllRefreshables { refreshAfterError("The game is not synchronized. Wrong coordinates:" +
                        "                                   Network Error:The game cannot be continued!")  }


            if (fromSupply) {
                rootService.playerService.drawNewCard()
            }
            if (rootService.mainGame!!.rotatable) {
                rootService.mainGame!!.players[rootService.mainGame!!.currentPlayer].handCards.last().angle = rotation
            }

            if (client!!.playerName == rootService.mainGame!!.players[(rootService.mainGame!!.currentPlayer+1)
                        % rootService.mainGame!!.players.size].name) {
                updateConnectionState(ConnectionState.PLAYING_TURN)
            }
            try {
                rootService.playerService.placeCard(x-1, y-1)
                if (rootService.mainGame!!.field.fieldCards[x-1][y-1].id != pTile!!.id) {
                    onAllRefreshables { refreshAfterError("The game is not synchronized. IDs are not equal:"
                            + "Network Error:The game cannot be continued!") }

                }
            } catch (e: IllegalStateException){
                println("draw pile is empty")
                e.printStackTrace()
            }
            rootService.playerService.nextPlayer()
        }
    }

    /**
     * sends a turn message to all players connected to the server
     * @param turn Information about the last turn made by the player.
     */
    fun sendTurnMessage(turn: GameTurn?) {
        val gameVerInfo = GameStateVerificationInfo(getTileInfo(),
                                                    getIdDrawList(),
                                                    getPlayerScores())
        val message = TurnMessage(posX = turn!!.posX+1,
                                  posY = turn.posY+1,
                                  fromSupply = !turn.isHandCard,
                                  rotation = turn.angle,
                                  gameStateVerificationInfo = gameVerInfo)
        updateConnectionState(ConnectionState.WAITING_FOR_TURN)
        client?.sendGameActionMessage(message)
    }


    /**
     * updates the connection state of this network service to the specified state
     */
    fun updateConnectionState(newState: ConnectionState) { this.connectionState = newState }


    /**
     * opens and hosts a new network game.
     * @param secret: the secret of the game
     * @param name: name of this client
     * @param sessionID: The Identifier of the game
     * @throws IllegalStateException if status != success or currently not waiting for a game creation response.
     */

    fun hostGame(secret: String, name: String, sessionID: String?) { //TODO GameID should be variable
        if (!connect(secret, name)) { error("Connection failed") }

        updateConnectionState(ConnectionState.CONNECTED)
        if (sessionID.isNullOrBlank()) { client?.createGame(GAME_ID, "Welcome!") }
        else { client?.createGame(GAME_ID, sessionID, "Welcome!") }
        updateConnectionState(ConnectionState.WAITING_FOR_HOST_CONFIRMATION)
    }


    /**
     * Joins a game which is hosted by another client.
     * @param secret: Secret string of the game
     * @param name: name of this client
     * @param sessionID: ID which identifies the game
     */
    fun joinGame(secret: String, name: String, sessionID: String){ //TODO GameID should be variable
        if (!connect(secret, name)) { error("Connection failed") }

        updateConnectionState(ConnectionState.CONNECTED)
        client?.joinGame(sessionID, "Hello!")
        updateConnectionState(ConnectionState.WAITING_FOR_JOIN_CONFIRMATION)
    }


    /**
     *  starts a game due [GameService], extracts the information of the main game, converts the information to a data
     *  JSON and sends a game init message to all joined clients
     *  @param players: List of the player, which are joined to the game session
     *  @param rotatable: should the players able to rotate their tiles?
     */
    fun startNewHostedGame(players: MutableList<Pair<String, entity.PlayerType>>, rotatable: Boolean) {
        check(connectionState == ConnectionState.READY_FOR_GAME){"Not ready for game"}

        val firstPlayer = players[0].first
        if(firstPlayer == client?.playerName) { updateConnectionState(ConnectionState.PLAYING_TURN) }
        else {updateConnectionState(ConnectionState.WAITING_FOR_TURN) }

        val cards = rootService.gameService.startGame(players = players, rotatable = rotatable, host = true)
        Thread.sleep(2000)

        val playerInfo = getPlayersForNetwork(rootService.mainGame!!.players)
        val networkTiles = getTilesForNetwork(cards!!)

        val message = GameInitMessage(rotationAllowed = rotatable, players = playerInfo, tileSupply = networkTiles)
        // ConnectionState should be sat before startGame()
        //updateConnectionState(ConnectionState.GAME_INITIALIZED)
        client?.sendGameActionMessage(message)
        /*if(rootService.mainGame!!.players[rootService.mainGame!!.currentPlayer].name == client!!.playerName){
            updateConnectionState(ConnectionState.PLAYING_TURN)
        }else{ updateConnectionState(ConnectionState.WAITING_FOR_TURN) }*/
        onAllRefreshables { refreshAfterStartGame() }
    }


    private fun getPlayersForNetwork(players:MutableList<Player>): MutableList<PlayerInfo> {
        val playerInfos = mutableListOf<PlayerInfo>()
        for (player in players){
            val playerInfo = PlayerInfo(player.name)
            playerInfos.add(playerInfo)
        }
        return playerInfos
    }


    private fun getTilesForNetwork(cards: MutableList<Card>): MutableList<Tile> {
        val tiles = mutableListOf<Tile>()
        for (card in cards){
            val connections = mutableListOf <ConnectionInfo>()
            for(route in card.routes){
                if(route.key < route.value){
                    connections.add(ConnectionInfo(route.key, route.value))
                }
            }
            val id = card.id
            val tile = Tile(id=id, connections=connections)
            tiles.add(tile)
        }
        return tiles
    }

    /**
     * get information about non-empty tiles (excluding the power station)
     */
     fun getTileInfo() : MutableList<TileInfo> {
        val fields = rootService.mainGame?.field?.fieldCards
        checkNotNull(fields)
        val tileInfoList = mutableListOf<TileInfo>()
        for (i in 0..7) {
            for (j in 0..7) {
                val card = fields[i][j]
                if (card.cardType == CardType.TRAFFIC) {
                    val tileInfo = TileInfo(i + 1, j + 1, card.id, card.angle)
                    tileInfoList.add(tileInfo)
                }
            }
        }
        return tileInfoList
    }

    private fun getIdDrawList() : List<Int> {
        val drawPile = rootService.mainGame!!.drawPile
        val listOfIds = mutableListOf<Int>()
        for (card in drawPile) {
            listOfIds.add(card.id)
        }
        return listOfIds
    }


    /**
     * get the player scores for sending over the network
     */
    fun getPlayerScores() : List<Int> {
        val playerScores = mutableListOf<Int>()
        val game = rootService.mainGame
        checkNotNull(game)
        for (player in game.players) {
            playerScores.add(player.points)
        }
        return playerScores
    }
}

