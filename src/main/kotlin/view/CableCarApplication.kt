package view

import entity.PlayerType
import service.*
import tools.aqua.bgw.animation.DelayAnimation
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.dialog.Dialog
import tools.aqua.bgw.dialog.DialogType
import tools.aqua.bgw.visual.ImageVisual

/**
 * Implementation of the BGW [BoardGameApplication] for the game "Cable Car".
 * @property rootService Central service from which all others are created/called
 * and also keeps the current active game.
 */
class CableCarApplication : BoardGameApplication("Cable Car"), Refreshable {
    var rootService = RootService()
    var players = mutableListOf<Pair<String, PlayerType>>()

    private var gameScene = GameScene(rootService, this).apply {
        menuButton.onMouseClicked = {
            this@CableCarApplication.showMenuScene(overlayMenu)
        }
        helpButton.onMouseClicked = { this@CableCarApplication.showMenuScene(helpMenu) }
    }

    var homeMenu = HomeMenu(this).apply {
        quitButton.onMouseClicked = {
            exit()
        }
    }

    private var overlayMenu = OverlayMenu(this).apply {
        exitButton.onMouseClicked = { exit() }
        continueGameButton.onMouseClicked = { this@CableCarApplication.hideMenuScene() }
    }

    private var helpMenu = HelpMenu().apply {
        continueGameButton.onMouseClicked = { this@CableCarApplication.hideMenuScene() }
    }

    var endScene =
        EndScene().apply {
            quitButton.onMouseClicked = { exit() }
        }


    var localMenu = LocalMenu(this)
    var lobbyMenu = LobbyMenu(this)
    var joinMenu = JoinMenu(this)
    var hostMenu = HostMenu(this)


    init {
        rootService.addRefreshables(this)
        rootService.addRefreshables(gameScene)
        rootService.addRefreshables(homeMenu)
        rootService.addRefreshables(localMenu)
        rootService.addRefreshables(joinMenu)
        rootService.addRefreshables(hostMenu)
        rootService.addRefreshables(lobbyMenu)
        rootService.addRefreshables(endScene)


        this.showMenuScene(homeMenu, 0)

        this.localMenu.goToLobbyButton.onMouseClicked = {
            if (localMenu.checkForSameName()) {
                showDialog(localMenu.sameName)
            } else if (!localMenu.checkForPlayerType()) {
                showDialog(localMenu.noPlayerType)
            } else {
                players = localMenu.createPlayerList()
                lobbyMenu.initLobbyMenu(players)
                lobbyMenu.background = ImageVisual("gamelobby.jpg")
                lobbyMenu.sessionID.text = "Local Game"
                showMenuScene(lobbyMenu)
            }
        }

        this.lobbyMenu.startGameButton.onMouseClicked = {
            if (!lobbyMenu.checkCorrectPlayerOrder()) {
                showDialog(lobbyMenu.playerOrder)
            } else {
                if (lobbyMenu.host) {
                    rootService.networkService.startNewHostedGame(
                        lobbyMenu.reorderPlayerList(),
                        lobbyMenu.rotatable
                    )
                } else {
                    rootService.gameService.startGame(lobbyMenu.reorderPlayerList(), lobbyMenu.rotatable)
                }
                this.lobbyMenu.removeComponents()
            }
        }







        this.overlayMenu.homeButton.onMouseClicked = {
            lobbyMenu.removeComponents()
            players.clear()
            if (rootService.networkService.connectionState == ConnectionState.WAITING_FOR_TURN ||
                rootService.networkService.connectionState == ConnectionState.PLAYING_TURN
            ) {
                rootService.networkService.disconnect()
            }
            localMenu.clearScene()
            hostMenu.clearScene()
            joinMenu.clearScene()
            lobbyMenu.host = false
            showMenuScene(homeMenu)
        }

        this.lobbyMenu.leaveButton.onMouseClicked = {
            this.lobbyMenu.removeComponents()
            players.clear()
            if (rootService.networkService.connectionState == ConnectionState.READY_FOR_GAME ||
                rootService.networkService.connectionState == ConnectionState.WAITING_FOR_PLAYERS ||
                rootService.networkService.connectionState == ConnectionState.WAITING_FOR_INIT
            ) {
                rootService.networkService.disconnect()
                if (!lobbyMenu.host) {
                    joinMenu.clearScene()
                } else {
                    hostMenu.clearScene()
                }
                lobbyMenu.host = false
            } else {
                localMenu.clearScene()
            }
            showMenuScene(homeMenu)

        }

        this.joinMenu.joinGameButton.onMouseClicked = {
            if (this.joinMenu.missingInputs()) {
                showDialog(this.joinMenu.missingInputs)
            } else {
                //write method to disable all text fields and buttons in lobbyMenu if player joins and not hosts
                //since only the host should be able to adjust the player order and start the game
                val playerInputs = this.joinMenu.retrievePlayerInputs()
                this.lobbyMenu.sessionID.text = joinMenu.getSessionID()
                rootService.networkService.joinGame(
                    name = playerInputs[0],
                    sessionID = playerInputs[1],
                    secret = "cable22"
                )
                if (playerInputs[2] == "Easy AI") {
                    rootService.networkService.client!!.isEasyAI = true
                }
                else if (playerInputs[2] == "Hard AI") {
                    rootService.networkService.client!!.isHardAI = true
                }
            }
        }

        this.hostMenu.startHostingButton.onMouseClicked = {
            if (hostMenu.missingInputs()) {
                showDialog(hostMenu.missingInputs)
            } else {
                val players = this.hostMenu.createPlayerList()
                this.lobbyMenu.initLobbyMenu(players)
                this.lobbyMenu.sessionID.text = hostMenu.getSessionID()
                lobbyMenu.host = true
                hostMenu.refreshAfterHostGame()
                if ( hostMenu.playerType == "Easy AI") {
                    rootService.networkService.client!!.isEasyAI = true
                }
                else if (hostMenu.playerType == "Hard AI") {
                    rootService.networkService.client!!.isHardAI = true
                }
            }
        }
    }

    override fun refreshAfterEndGame() {
        this.showMenuScene(endScene)
    }

    override fun refreshAfterError(msg: String) {
        val dialog: Dialog = Dialog(
            DialogType.ERROR,
            header = msg.split(":")[2],
            title = msg.split(":")[1],
            message = msg.split(":")[0],
        ).apply { onWindowClosed = { this@CableCarApplication.exit() } }
        showDialog(dialog)

    }

    /**
     * sends an error message if too many players joined the lobby
     */
    override fun refreshAfterPlayerJoined(sender: String) {
        if (players.size >= 6) {
            showDialog(lobbyMenu.tooManyPlayers)
        }
    }

    /**
     * helper function to convert String into PlayerType
     */
    fun stringToPlayerType(playerType: String): PlayerType? {
        return when (playerType) {
            "Player" -> PlayerType.HUMAN
            "Easy AI" -> PlayerType.EASYAI
            "Hard AI" -> PlayerType.HARDAI
            else -> {
                return null
            }
        }
    }

    /**
     * refresh after a game starts
     */
    override fun refreshAfterStartGame() {
        hideMenuScene()
        showGameScene(gameScene)

        gameScene.playAnimation(DelayAnimation(duration = (rootService.mainGame!!.simulationVit * 1000).toInt()).apply {
            onFinished = {

                val isNetworkGame = rootService.networkService.client != null

                if((rootService.mainGame!!.players[0].playerType == PlayerType.EASYAI ||
                            rootService.mainGame!!.players[0].playerType == PlayerType.HARDAI) && !isNetworkGame){
                    rootService.playerService.nextPlayer()
                }
                if (isNetworkGame){
                    if ((rootService.networkService.client!!.isEasyAI || rootService.networkService.client!!.isHardAI)
                        && rootService.networkService.connectionState == ConnectionState.PLAYING_TURN
                    ) {
                        val game = rootService.mainGame
                        if (rootService.networkService.client!!.isEasyAI) {
                            rootService.aiService.randomTurn()

                            game!!.field.futureTurns.clear()
                            rootService.networkService.sendTurnMessage(rootService.mainGame!!.field.lastTurns.peek())
                            rootService.networkService.updateConnectionState(ConnectionState.WAITING_FOR_TURN)
                            game.currentPlayer = (game.currentPlayer + 1) % game.players.size
                        }
                        else {
                            rootService.aiService.smartTurn()
                            rootService.playerService.secondCard = false

                            game!!.field.futureTurns.clear()
                            rootService.networkService.sendTurnMessage(rootService.mainGame!!.field.lastTurns.peek())
                            rootService.networkService.updateConnectionState(ConnectionState.WAITING_FOR_TURN)
                            game!!.currentPlayer = (game.currentPlayer + 1) % game!!.players.size

                        }
                    }
                }

            }
        })
        gameScene.unlock()

    }

}
