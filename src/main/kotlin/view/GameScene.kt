package view

import entity.*
import service.CardImageLoader
import service.ConnectionState
import service.RootService
import tools.aqua.bgw.animation.DelayAnimation
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import java.awt.Color
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.visual.*
import javax.imageio.ImageIO


/**
 * this is the game scene where the game runs.
 * the scene has several buttons that are required for the game.
 * @param rootService Reference to our RootService
 */

class GameScene(private val rootService: RootService, private val cableCarApplication: CableCarApplication) :
    BoardGameScene(
        width = 5425, height = 2995,
        background = ColorVisual(213, 203, 175)
    ), Refreshable {
    private var isNetworkGame = false

    private var localPlayerIndexInNetworkGame: Int? = null

    private val placeCardButton: GameButton = GameButton(
        width = 1000, height = 200,
        posX = 20, posY = 260,
        text = "Place Card", font = Font(70, color = Color(0, 0, 0)),
        backgroundColor = ColorVisual(136, 221, 136)
    ).apply { isDisabled = true; onMouseClicked = { onPlaceCard() } }

    private val viewData = ViewData(placeCardButton)

    private val playerNameLabel = PlayerNameLabel(width = width, height = height)

    //button to exit or continue the game or go to home-menu
    val menuButton: GameButton = GameButton(
        width = 500, height = 200,
        posX = 4400, posY = 20,
        text = "Menu", font = Font(70, color = Color(0, 0, 0)),
        backgroundColor = ColorVisual(139, 69, 19)
    ).apply {
        isDisabled = false
    }

    //button to get help and description of the game
    val helpButton: GameButton = GameButton(
        width = 500, height = 200,
        posX = 4905, posY = 20,
        text = "Help?", font = Font(70, color = Color(0, 0, 0)),
        backgroundColor = ColorVisual(139, 69, 19)
    )

    private val endTurnButton: GameButton = GameButton(
        width = 1000, height = 200,
        posX = 4400, posY = 2755,
        text = "End Turn", font = Font(70, color = Color(0, 0, 0)),
        backgroundColor = ColorVisual(136, 221, 136)
    ).apply { isDisabled = true }
    private val undoButton: TransparentButton = TransparentButton(
        posX = 20, posY = 20,
        font = Font(70, color = Color(0, 0, 0)),
//        colorVisual =  ColorVisual(136, 221, 136),
        imageVisual = ImageVisual(image = ImageIO.read(CardImageLoader::class.java.getResource("/undo.png")))

    ).apply {
        setIsDisable = true
        onMouseClicked = { onUndo() }
    }

    private val redoButton: TransparentButton = TransparentButton(
        posX = 270, posY = 20,
        font = Font(70, color = Color(0, 0, 0)),
//        colorVisual = ColorVisual(221, 136, 136),
        imageVisual = ImageVisual(image = ImageIO.read(CardImageLoader::class.java.getResource("/redo.png")))
    ).apply {
        setIsDisable = true
        onMouseClicked = { onRedo() }
    }

    private val zoomButton = ZoomButton(posX = 520, posY = 20)

    private val rotateCardButton = TransparentButton(
        posX = 770, posY = 20,
        imageVisual = ImageVisual(
            image = ImageIO.read(CardImageLoader::class.java.getResource("/rotateCardButton.png"))
        )
    ).apply { onMouseClicked = { onRotateCard() } }


    private val drawCardButton: GameButton = GameButton(
        width = 1000, height = 200,
        posX = 20, posY = 500,
        text = "Draw Card", font = Font(70, color = Color(0, 0, 0)),
        backgroundColor = ColorVisual(136, 221, 136)
    ).apply {
        onMouseClicked = { rootService.playerService.drawNewCard() }
    }

    private val drawPileLayout = CardStack<CardView>(
        posX = 50,
        posY = 740,
        alignment = Alignment.CENTER,
        width = 264,
        height = 264,
        visual = CompoundVisual(
            ColorVisual(Color(255, 255, 255, 50)),
            TextVisual("Draw Pile"),
        )
    )
    private val drawPileSizeLabel = Label(
        posX = 50,
        posY = 1100,
        width = 264,
        alignment = Alignment.CENTER,
        font = Font(size = 42, fontWeight = Font.FontWeight.BOLD)
    )

    private val board = Label(
        posX = 1156, posY = 0,
        width = 3112, height = 2995,
        visual = ImageVisual(
            image = ImageIO.read(CardImageLoader::class.java.getResource("/game_board.jpg"))
        )
    )

    private val trains = Trains()

    private val magnifyingGlass = MagnifyingGlass(posX = 0, posY = height - 2 * 264, zoomButton)

    private val grid: GridPane<CardSlot> = GridPane<CardSlot>(5425 / 2, 2995 / 2, 8, 8).apply {
        this.setColumnWidths(264)
        this.setRowHeights(264)
        forEach {
            val cardSlot = CardSlot(
                posX = 0, posY = 0,
                magnifyingGlass = magnifyingGlass,
                viewData = viewData, column = it.columnIndex, row = it.rowIndex
            )
            this[it.columnIndex, it.rowIndex] = cardSlot
            val isVerticalMiddle = it.columnIndex == 3 || it.columnIndex == 4
            val isHorizontalMiddle = it.rowIndex == 3 || it.rowIndex == 4
            if (isVerticalMiddle && isHorizontalMiddle) {
                this[it.columnIndex, it.rowIndex]!!.isVisible = false
            }

        }

//        this[5, 5] = Button(text = "grid", visual = ColorVisual.CYAN)
    }


    private val currentPlayerHandCard = HandCard(
        posX = 0, posY = 0, width = 264 * 2, height = 264 * 2,
        magnifyingGlass = magnifyingGlass, viewData = viewData
    ).apply {
        viewData.init()
        //this.cardView = viewData.allCards[0]
    }

    private val currentPlayerDrawnCard = HandCard(
        posX = 264 * 2 + 30, posY = 0, width = 264 * 2, height = 264 * 2,
        magnifyingGlass = magnifyingGlass, viewData = viewData
    ).apply {
        //this.cardView = viewData.allCards[1]
    }

    private val currentPlayerHeldCard = Pane<HandCard>(
        posX = 30, posY = 1800, width = 30 + 264 * 4,
        height = 264 * 2, visual = ColorVisual.TRANSPARENT
    ).apply {
        this.addAll(
            currentPlayerHandCard,
            currentPlayerDrawnCard
        )
    }

    private val next1PlayerHandCard = HandCard(
        posX = 0, posY = 0,
        magnifyingGlass = magnifyingGlass, viewData = viewData
    ).apply {
    }

    private val next2PlayerHandCard = HandCard(
        posX = 264 + 200, posY = 0,
        magnifyingGlass = magnifyingGlass, viewData = viewData
    ).apply {
    }


    private val next3PlayerHandCard = HandCard(
        posX = 0, posY = 264 + 200,
        magnifyingGlass = magnifyingGlass, viewData = viewData
    ).apply {
    }


    private val next4PlayerHandCard = HandCard(
        posX = 264 + 200, posY = 264 + 200,
        magnifyingGlass = magnifyingGlass, viewData = viewData
    ).apply {
    }


    private val next5PlayerHandCard = HandCard(
        posX = 0, posY = (264 + 200) * 2,
        magnifyingGlass = magnifyingGlass, viewData = viewData
    ).apply {
    }

    private val otherPlayerHeldCards = mutableListOf(
        next1PlayerHandCard, next2PlayerHandCard, next3PlayerHandCard,
        next4PlayerHandCard, next5PlayerHandCard
    )

    private val otherPlayerHeldCardsPane = Pane<HandCard>(
        posX = this.width - 940, posY = 800, width = 30 + 264 * 4,
        height = 264 * 2, visual = ColorVisual.TRANSPARENT
    ).apply {
        this.addAll(
            next1PlayerHandCard,
            next2PlayerHandCard,
            next3PlayerHandCard,
            next4PlayerHandCard,
            next5PlayerHandCard
        )
    }
    private var comboBox1 = ComboBox<String>(
        width = 1150, posX = 4300, posY = 240, font = Font(60), prompt = "Choose the Simulation Speed"
    ).apply {
        this.items = mutableListOf(prompt, "Low", "Middle", "High")
        isVisible = false
    }
    private val simulateSpeed = SimulateSpeed()

    init {
        addComponents(
            board,
            playerNameLabel,
            trains,
            grid,
            magnifyingGlass,
            helpButton, menuButton,
            comboBox1,
            simulateSpeed,
            redoButton,
            undoButton,
            placeCardButton,
            drawCardButton,
            drawPileLayout,
            drawPileSizeLabel,
            rotateCardButton,
            zoomButton,
            currentPlayerHeldCard,
            otherPlayerHeldCardsPane,
        )
    }


    ///////////////////////// create and update functions //////////////////////////////////
    /*private fun createTrains(trains: MutableList<Pair<Int, Boolean>>) {

    }*/
    private fun updateHandCards(game: MainGame, targetPlayerIndex: Int) {
        when (game.players[targetPlayerIndex].handCards.size) {
            0 -> {
                currentPlayerHandCard.cardView = null
                currentPlayerDrawnCard.cardView = null
            }

            1 -> {
                currentPlayerHandCard.cardView = viewData.allCards[game.players[targetPlayerIndex].handCards[0].id]
                    .apply { rotation = game.players[targetPlayerIndex].handCards[0].angle.toDouble() }
                currentPlayerDrawnCard.cardView = null
            }

            2 -> {
                currentPlayerHandCard.cardView = viewData.allCards[game.players[targetPlayerIndex].handCards[0].id]
                    .apply { rotation = game.players[targetPlayerIndex].handCards[0].angle.toDouble() }
                currentPlayerDrawnCard.cardView = viewData.allCards[game.players[targetPlayerIndex].handCards[1].id]
                    .apply { rotation = game.players[targetPlayerIndex].handCards[1].angle.toDouble() }
            }
        }
        for (i in game.players.indices) {
            val nextPlayerIndex = (i + targetPlayerIndex) % game.players.size
            val handCardsAvailable = nextPlayerIndex != targetPlayerIndex
                    && game.players[nextPlayerIndex].handCards.size > 0
            val handCardsNotAvailable = nextPlayerIndex != targetPlayerIndex
                    && game.players[nextPlayerIndex].handCards.size <= 0
            if (handCardsAvailable) {
                otherPlayerHeldCards[i - 1].cardView = viewData.allCards[game.players[nextPlayerIndex]
                    .handCards[0].id]
                    .apply {
                        rotation = game.players[nextPlayerIndex].handCards[0].angle.toDouble()
                    }
            } else if (handCardsNotAvailable) {
                otherPlayerHeldCards[i - 1].cardView = null
            }
            // dissolving complexity
//            if (nextPlayerIndex != targetPlayerIndex) {
//                if (game.players[nextPlayerIndex].handCards.size > 0) {
//                    otherPlayerHeldCards[i - 1].cardView = viewData.allCards[game.players[nextPlayerIndex]
//                        .handCards[0].id]
//                        .apply { rotation = game.players[nextPlayerIndex].handCards[0].angle.toDouble() }
//                } else {
//                    otherPlayerHeldCards[i - 1].cardView = null
//                }
//            }
        }
    }

    private fun updateDrawPile(game: MainGame) {
        drawPileLayout.clear()
        game.drawPile.forEach {
            val cardView = viewData.allCards[it.id].apply { width = 264.0; height = 264.0 }
            drawPileLayout.add(cardView)
        }
    }

    private fun updateField(game: MainGame) {
        val cardLayout = game.field.fieldCards
        for (i in cardLayout.indices) {
            for (j in cardLayout[i].indices) {
                updateView(cardLayout = cardLayout, i = i, j = j)
//                if (cardLayout[i][j].cardType == CardType.EMPTY) {
//                    grid[i, j]!!.apply {
//                        cardView = null
//                    }
//                } else if (cardLayout[i][j].cardType == CardType.TRAFFIC) {
//                    grid[i, j]!!.apply {
//                        cardView = viewData.allCards[cardLayout[i][j].id]
//                            .apply { rotation = cardLayout[i][j].angle.toDouble() }
//                    }
//                }
            }
        }
    }

    private fun updateView(cardLayout: MutableList<MutableList<Card>>, i: Int, j: Int) {

        if (cardLayout[i][j].cardType == CardType.EMPTY) {
            grid[i, j]!!.apply {
                cardView = null
            }
        } else if (cardLayout[i][j].cardType == CardType.TRAFFIC) {
            grid[i, j]!!.apply {
                cardView = viewData.allCards[cardLayout[i][j].id]
                    .apply { rotation = cardLayout[i][j].angle.toDouble() }
            }
        }
    }


    ////////////////////////// refreshes ///////////////////////////////
    override fun refreshAfterStartGame() {
        val game = rootService.mainGame
        checkNotNull(game) { "No game is running" }
        isNetworkGame = rootService.networkService.client != null
        if (isNetworkGame) {
            if (rootService.networkService.connectionState == ConnectionState.PLAYING_TURN) {
                drawCardButton.isDisabled = false
                placeCardButton.isDisabled = true
                rotateCardButton.isDisabled = false
                viewData.theSelectedCardCanBePlace = true
            } else {
                drawCardButton.isDisabled = false
                placeCardButton.isDisabled = true
                rotateCardButton.isDisabled = true
                viewData.theSelectedCardCanBePlace = false
            }
        }

        // If the game does not allow the player to rotate the cards, hide the [rotateCardButton]
        rotateCardButton.isVisible = game.rotatable
        val playerNr = game.players.size

        // The number of cards hold by other players adapts to the number of players
        for (i in otherPlayerHeldCards.indices) {
            otherPlayerHeldCards[i].isVisible = i < playerNr - 1
        }

        // Trains:
        trains.updateAfterStartGame(game.field.trains)
        //update draw pile
        updateDrawPile(game)
        drawPileSizeLabel.text = game.drawPile.size.toString()

        // config name Label
        playerNameLabel.updateAfterStartGame(game.players)

        // Get the game type (local or online)
        isNetworkGame = rootService.networkService.client != null

        // set the visibility of the Undo-/RedoButton (Network Game not available)
        undoButton.isVisible = !isNetworkGame
        redoButton.isVisible = undoButton.isVisible

        // init simulateSpeed setter
        simulateSpeed.init(rootService.mainGame)

        // init viewData
        viewData.init()

        if (isNetworkGame) {
            val localPlayerName = rootService.networkService.client!!.playerName
            val players = rootService.mainGame!!.players
            for (i in players.indices) {
                if (localPlayerName == players[i].name) {
                    localPlayerIndexInNetworkGame = i
                }
            }
            /*rootService.networkService.players!!.forEach {
                if (localPlayerName == it.first) {

                }
            }*/
        }

        // Rearrange the button layout
        val posX = mutableListOf(20.0, 270.0, 520.0, 770.0)
        val button = mutableListOf(undoButton, redoButton, zoomButton, rotateCardButton)
        if (!undoButton.isVisible) {
            button.remove(undoButton)
            button.remove(redoButton)
        }
        if (!rotateCardButton.isVisible) {
            button.remove(rotateCardButton)
        }
        for (i in button.indices) {
            button[i].posX = posX[i]
        }

        // to avoid errors in endScene
        cableCarApplication.endScene.namePlayer1.text = ""
        cableCarApplication.endScene.namePlayer2.text = ""
        cableCarApplication.endScene.namePlayer3.text = ""
        cableCarApplication.endScene.namePlayer4.text = ""
        cableCarApplication.endScene.namePlayer5.text = ""
        cableCarApplication.endScene.namePlayer6.text = ""
        cableCarApplication.endScene.gameResult1.text = ""
        cableCarApplication.endScene.gameResult2.text = ""
        cableCarApplication.endScene.gameResult3.text = ""
        cableCarApplication.endScene.gameResult4.text = ""
        cableCarApplication.endScene.gameResult5.text = ""
        cableCarApplication.endScene.gameResult6.text = ""
        this.refreshAfterTurnEnds()
    }

    override fun refreshAfterDraw() {
        val game = rootService.mainGame
        checkNotNull(game) { "No game is running" }
        drawCardButton.isDisabled = true
        val has2Cards = game.players[game.currentPlayer].handCards.size == 2
        if (has2Cards) {
            updateHandCards(game, game.currentPlayer)
            updateDrawPile(game)
            updateField(game)
        }
        drawPileSizeLabel.text = game.drawPile.size.toString()
    }

    override fun refreshAfterTurnEnds() {
        val game = rootService.mainGame
        if (game != null) {
            // update trains
            trains.updateAfterTurnEnd(game.field.trains)
            updateDrawPile(game)
            updateField(game)

            if (isNetworkGame) {
                updateHandCards(game, localPlayerIndexInNetworkGame!!)

                // update name Label
                playerNameLabel.updateAfterTurnEndNetwork(
                    game.players, game.currentPlayer, localPlayerIndexInNetworkGame!!
                )
                when (rootService.networkService.connectionState) {
                    ConnectionState.PLAYING_TURN -> {
                        drawCardButton.isDisabled = false
                        placeCardButton.isDisabled = true
                        rotateCardButton.isDisabled = false
                        viewData.theSelectedCardCanBePlace = true
                    }

                    ConnectionState.WAITING_FOR_TURN -> {
                        drawCardButton.isDisabled = true
                        placeCardButton.isDisabled = true
                        rotateCardButton.isDisabled = true
                        viewData.theSelectedCardCanBePlace = false
                    }

                    else -> {
                        throw IllegalStateException("Network error")
                    }
                }

                // If local player is an AI, disable the place card function. (fun1)
                if (game.players[localPlayerIndexInNetworkGame!!].playerType != PlayerType.HUMAN) {
                    viewData.theSelectedCardCanBePlace = false
                    drawCardButton.isDisabled = true
                }
            } else {
                updateHandCards(game, game.currentPlayer)
                viewData.theSelectedCardCanBePlace = true
                drawCardButton.isDisabled = false
                placeCardButton.isDisabled = true

                undoButton.setIsDisable = game.field.lastTurns.empty()
                redoButton.setIsDisable = game.field.futureTurns.empty()

                // update name Label
                playerNameLabel.updateAfterTurnEndLocal(game.players, game.currentPlayer)

                // If current player is an AI, disable the place card function. (fun1)
                if (game.players[game.currentPlayer].playerType != PlayerType.HUMAN) {
                    viewData.theSelectedCardCanBePlace = false
                    drawCardButton.isDisabled = true
                }
            }

            //why playerHandCard in viewData ??
            /*for (i in game.players.indices) {
                val cardId = game.players[i].handCards[0].id
                viewData.playerHandCard[i] = viewData.allCards[cardId]
            }*/
        }

        this.playAnimation(DelayAnimation(duration = (rootService.mainGame!!.simulationVit * 1000).toInt()).apply {
            onFinished = {

                val isNetworkGame = rootService.networkService.client != null

                if ((rootService.mainGame!!.players[rootService.mainGame!!.currentPlayer].playerType == PlayerType.EASYAI
                            || rootService.mainGame!!.players[rootService.mainGame!!.currentPlayer].playerType == PlayerType.HARDAI)
                    && !isNetworkGame
                ) {
                    rootService.playerService.nextPlayer()
                }
                if (isNetworkGame) {
                    if ((rootService.networkService.client!!.isEasyAI
                                || rootService.networkService.client!!.isHardAI)
                        && rootService.networkService.connectionState == ConnectionState.PLAYING_TURN
                    ) {
                        rootService.playerService.nextPlayer()
                    }
                }

            }
        })
    }

    override fun refreshAfterRotate() {
        val game = rootService.mainGame
        checkNotNull(game)
        updateHandCards(game, game.currentPlayer)
    }

    override fun refreshAfterUndo() {
        this.refreshAfterTurnEnds()
    }

    override fun refreshAfterRedo() {
        this.refreshAfterTurnEnds()
    }

    override fun refreshAfterEndGame() {
        val game = rootService.mainGame
        checkNotNull(game)
        val resultKeyList: List<Player> = ArrayList(rootService.gameService.computeWinner().keys)
        val resultValueList: List<Int> = ArrayList(rootService.gameService.computeWinner().values)
        when (game.players.size) {
            2 -> {
                cableCarApplication.endScene.namePlayer1.text = "${resultKeyList[1].name}:"
                cableCarApplication.endScene.gameResult1.text =
                    "${resultValueList[1]} Points"
                cableCarApplication.endScene.namePlayer2.text = "2. ${resultKeyList[0].name}:"
                cableCarApplication.endScene.gameResult2.text =
                    "${resultValueList[0]} Points"
            }

            3 -> {
                cableCarApplication.endScene.namePlayer1.text = "${resultKeyList[2].name}:"
                cableCarApplication.endScene.gameResult1.text =
                    "${resultValueList[2]} Points"
                cableCarApplication.endScene.namePlayer2.text = "2. ${resultKeyList[1].name}:"
                cableCarApplication.endScene.gameResult2.text =
                    "${resultValueList[1]} Points"
                cableCarApplication.endScene.namePlayer3.text = "3. ${resultKeyList[0].name}:"
                cableCarApplication.endScene.gameResult3.text =
                    "${resultValueList[0]} Points"
            }

            4 -> {
                cableCarApplication.endScene.namePlayer1.text = "${resultKeyList[3].name}:"
                cableCarApplication.endScene.gameResult1.text =
                    "${resultValueList[3]} Points"
                cableCarApplication.endScene.namePlayer2.text = "2. ${resultKeyList[2].name}:"
                cableCarApplication.endScene.gameResult2.text =
                    "${resultValueList[2]} Points"
                cableCarApplication.endScene.namePlayer3.text = "3. ${resultKeyList[1].name}:"
                cableCarApplication.endScene.gameResult3.text =
                    "${resultValueList[1]} Points"
                cableCarApplication.endScene.namePlayer4.text = "4. ${resultKeyList[0].name}:"
                cableCarApplication.endScene.gameResult4.text =
                    "${resultValueList[0]} Points"
            }

            5 -> {
                cableCarApplication.endScene.namePlayer1.text = "${resultKeyList[4].name}:"
                cableCarApplication.endScene.gameResult1.text =
                    "${resultValueList[4]} Points"
                cableCarApplication.endScene.namePlayer2.text = "2. ${resultKeyList[3].name}:"
                cableCarApplication.endScene.gameResult2.text =
                    "${resultValueList[3]} Points"
                cableCarApplication.endScene.namePlayer3.text = "3. ${resultKeyList[2].name}:"
                cableCarApplication.endScene.gameResult3.text =
                    "${resultValueList[2]} Points"
                cableCarApplication.endScene.namePlayer4.text = "4. ${resultKeyList[1].name}:"
                cableCarApplication.endScene.gameResult4.text =
                    "${resultValueList[1]} Points"
                cableCarApplication.endScene.namePlayer5.text = "5. ${resultKeyList[0].name}:"
                cableCarApplication.endScene.gameResult5.text =
                    "${resultValueList[0]} Points"
            }

            6 -> {
                cableCarApplication.endScene.namePlayer1.text = "${resultKeyList[5].name}:"
                cableCarApplication.endScene.gameResult1.text =
                    "${resultValueList[5]} Points"
                cableCarApplication.endScene.namePlayer2.text = "2. ${resultKeyList[4].name}:"
                cableCarApplication.endScene.gameResult2.text =
                    "${resultValueList[4]} Points"
                cableCarApplication.endScene.namePlayer3.text = "3. ${resultKeyList[3].name}:"
                cableCarApplication.endScene.gameResult3.text =
                    "${resultValueList[3]} Points"
                cableCarApplication.endScene.namePlayer4.text = "4. ${resultKeyList[2].name}:"
                cableCarApplication.endScene.gameResult4.text =
                    "${resultValueList[2]} Points"
                cableCarApplication.endScene.namePlayer5.text = "5. ${resultKeyList[1].name}:"
                cableCarApplication.endScene.gameResult5.text =
                    "${resultValueList[1]} Points"
                cableCarApplication.endScene.namePlayer6.text = "6. ${resultKeyList[0].name}:"
                cableCarApplication.endScene.gameResult6.text =
                    "${resultValueList[0]} Points"
            }
        }
    }


    /////////////////////////// Buttons Functions /////////////////////////////
    private fun onPlaceCard() {
        checkNotNull(viewData.selectedCardSlot) { "No selected slot" }
        val game = rootService.mainGame
        checkNotNull(game) { "No game is running" }
        try {
            rootService.playerService.placeCard(viewData.selectedCardSlot!!.column, viewData.selectedCardSlot!!.row)
            drawCardButton.isDisabled = true
            placeCardButton.isDisabled = true
            viewData.theSelectedCardCanBePlace = false
            if (isNetworkGame) {
                rootService.networkService.sendTurnMessage(rootService.mainGame!!.field.lastTurns.peek())
            }
            rootService.playerService.nextPlayer()
        } catch (e: IllegalArgumentException) {
            // illegal place
            println(e.message)

        } catch (e: IllegalStateException) {
            // draw pile is empty
            println(e.message)
            drawCardButton.isDisabled = true
            placeCardButton.isDisabled = true
            endTurnButton.isDisabled = false
            viewData.theSelectedCardCanBePlace = false
            if (isNetworkGame) {
                rootService.networkService.sendTurnMessage(rootService.mainGame!!.field.lastTurns.peek())
            }
            rootService.playerService.nextPlayer()
        }
    }

    private fun onRotateCard() {
        val game = rootService.mainGame
        checkNotNull(game)
        rootService.playerService.rotate()
    }

    private fun onUndo() {
        val game = rootService.mainGame
        checkNotNull(game)
        rootService.undoRedoService.undo()
    }

    private fun onRedo() {
        val game = rootService.mainGame
        checkNotNull(game)
        rootService.undoRedoService.redo()
    }


}
