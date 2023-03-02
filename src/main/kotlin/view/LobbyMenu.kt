package view

import entity.PlayerType
import tools.aqua.bgw.components.uicomponents.CheckBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.dialog.Dialog
import tools.aqua.bgw.dialog.DialogType
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color

/**
 * displayed when either a local game or network game has been created.
 * In this lobby each player is represented by his name and has a unique number which tells the Player when
 * his turn starts. These numbers can be manually changed or randomized. If it's a network game only the host can
 * do these actions as well as starting the game and deciding if the cards are rotatable or not.
 * @param cableCarApplication is an object of class [CableCarApplication]
 */
class LobbyMenu(private val cableCarApplication: CableCarApplication) : MenuScene(1160, 900),
    Refreshable {

    var host = false

    /**
     * error-dialogs if there are less than two players or some players have the same name
     */
    val playerOrder: Dialog = Dialog(
        DialogType.ERROR,
        title = "Player order",
        header = "Correction required",
        message = "The ordering of players is incorrect!"
    )

    val tooManyPlayers: Dialog = Dialog(
        DialogType.ERROR,
        title = "Too many Players",
        header = "Correction required",
        message = "There are too many players in this lobby!"
    )

    /**
     * all necessary labels
     */
    private var playerNameLabels = mutableListOf(
        Label(
            posX = 300,
            posY = 300,
            width = 250,
            text = "",
            font = Font(size = 25, fontWeight = Font.FontWeight.BOLD, color = Color.WHITE),
            alignment = Alignment.CENTER
        ),
        Label(
            posX = 300,
            posY = 350,
            width = 250,
            text = "",
            font = Font(size = 25, fontWeight = Font.FontWeight.BOLD, color = Color.WHITE),
            alignment = Alignment.CENTER
        ),
        Label(
            posX = 300,
            posY = 400,
            width = 250,
            text = "",
            font = Font(size = 25, fontWeight = Font.FontWeight.BOLD, color = Color.WHITE),
            alignment = Alignment.CENTER
        ),
        Label(
            posX = 300,
            posY = 450,
            width = 250,
            text = "",
            font = Font(size = 25, fontWeight = Font.FontWeight.BOLD, color = Color.WHITE),
            alignment = Alignment.CENTER
        ),
        Label(
            posX = 300,
            posY = 500,
            width = 250,
            text = "",
            font = Font(size = 25, fontWeight = Font.FontWeight.BOLD, color = Color.WHITE),
            alignment = Alignment.CENTER
        ),
        Label(
            posX = 300,
            posY = 550,
            width = 250,
            text = "",
            font = Font(size = 25, fontWeight = Font.FontWeight.BOLD, color = Color.WHITE),
            alignment = Alignment.CENTER
        )
    )

    var sessionID = Label(
        posX = 615,
        posY = 104,
        width = 250,
        text = "",
        font = Font(size = 25, fontWeight = Font.FontWeight.BOLD, color = Color.BLACK),
        alignment = Alignment.CENTER_LEFT
    )

    /**
     * list of Text fields for input of player names
     */
    private var tfOrder = mutableListOf(
        TextField(posX = 725, posY = 300, prompt = "1", width = 26, height = 20),
        TextField(posX = 725, posY = 350, prompt = "2", width = 26, height = 20),
        TextField(posX = 725, posY = 400, prompt = "3", width = 26, height = 20),
        TextField(posX = 725, posY = 450, prompt = "4", width = 26, height = 20),
        TextField(posX = 725, posY = 500, prompt = "5", width = 26, height = 20),
        TextField(posX = 725, posY = 550, prompt = "6", width = 26, height = 20)
    )

    /**
     * strings to save player names and player types
     */
    private var order = mutableListOf("1", "2", "3", "4", "5", "6")
    var rotatable = false

    /**
     * checkbox to see if game mode is rotatable or not
     */
    val cbRotatable = CheckBox(posX = 500, posY = 652, width = 30, text = "", alignment = Alignment.CENTER)

    /**
     * all Buttons
     */
    val startGameButton = GameButton(
        200, 70, 100, 770, "Start Game",
        Font(size = 25, fontWeight = Font.FontWeight.BOLD), ColorVisual(144, 240, 142)
    )

    val leaveButton = GameButton(
        200, 70, 860, 770, "Leave Lobby",
        Font(size = 25, fontWeight = Font.FontWeight.BOLD), ColorVisual(137, 4, 6)
    )

    private val randomizeButton = GameButton(
        90, 25, 695, 600, "Randomize",
        Font(size = 13, fontWeight = Font.FontWeight.BOLD), ColorVisual(242, 202, 41)
    ).apply {
        onMouseClicked = {
            randomize()
        }
    }

    init {
        //background = ImageVisual("gamelobby.jpg")
        opacity = 1.0
        cbRotatable.isCheckedProperty.addListener { _, value -> rotatable = value }
    }

    /**
     * creates a random player order - the new order is displayed in the prompt of the corresponding
     * text field. The [order] list is also randomized so that the actual value is saved.
     */
    private fun randomize() {
        val randomList = mutableListOf<Int>()
        var size = cableCarApplication.players.size
        if (size > 6) {
            size = 6
        }
        for (i in 0 until size) {
            randomList.add(i + 1)
        }
        randomList.shuffle()
        for (i in 0 until randomList.size) {
            tfOrder[i].text = ""
            tfOrder[i].prompt = randomList[i].toString()
            order[i] = randomList[i].toString()
        }
    }

    /**
     * helper function to initialize labels and components of the lobby menu
     */
    fun initLobbyMenu(players: MutableList<Pair<String, PlayerType>>) {
        for (i in 0 until players.size) {
            playerNameLabels[i].text = players[i].first
            addComponents(
                playerNameLabels[i],
                tfOrder[i]
            )
            tfOrder[i].textProperty.addListener { _, value -> order[i] = value }
        }
        startGameButton.isDisabled = cableCarApplication.players.size == 1
        addComponents(
            cbRotatable,
            startGameButton,
            randomizeButton,
            leaveButton,
            sessionID
        )
    }

    /**
     * helper function to only initialize names of players - used in [JoinMenu] since those players
     * should not be able to start a game or adjust the order of players
     * if more than the allowed number of players are in the lobby, the person who joins can only see the allowed
     * participants and not himself or any other player that is over the limit
     */
    fun initPlayerNames(players: MutableList<Pair<String, PlayerType>>) {
        var size = cableCarApplication.players.size
        if (cableCarApplication.players.size > 6) {
            size = 6
        }
        for (i in 0 until size) {
            playerNameLabels[i].text = players[i].first
            addComponents(playerNameLabels[i])
        }
        addComponents(
            startGameButton,
            leaveButton,
            sessionID
        )
        startGameButton.isDisabled = true
    }

    /**
     * helper function to determine if input of player order is correct or not - called in [CableCarApplication]
     */
    fun checkCorrectPlayerOrder(): Boolean {
        val correctInputs = mutableListOf<String>()
        val isInCorrectInputs = mutableListOf<Boolean>()

        //adding players.size many values to both mutableLists
        for (i in 1 until cableCarApplication.players.size + 1) {
            correctInputs.add(i.toString())
            isInCorrectInputs.add(false)
        }

        //checking if inputs of order (mutableList) are correct numbers
        for (i in 0 until cableCarApplication.players.size) {
            for (j in 0 until cableCarApplication.players.size) {
                if (order[i] == correctInputs[j]) {
                    isInCorrectInputs[i] = true
                }
                //checking that no number is the same
                if (i != j && order[i] == order[j]) {
                    return false
                }
            }
            if (!isInCorrectInputs[i]) {
                return false
            }
        }
        return true
    }

    /**
     * helper function that reorders the players list according to the new order - is called from
     * [CableCarApplication] when a new game starts
     */
    fun reorderPlayerList(): MutableList<Pair<String, PlayerType>> {
        val reorderedPlayers = mutableListOf<Pair<String, PlayerType>>()
        for (i in 0 until cableCarApplication.players.size) {
            for (j in 0 until cableCarApplication.players.size) {
                if (order[j] == (i + 1).toString()) {
                    reorderedPlayers.add(cableCarApplication.players[j])
                }
            }
        }
        return reorderedPlayers
    }

    /**
     * removes already initialized components in order to change the number of players or update the player information
     */
    fun removeComponents() {
        for (i in 0 until 6) {
            removeComponents(
                playerNameLabels[i],
                tfOrder[i]
            )
            val reorder = i + 1
            tfOrder[i].text = ""
            order[i] = reorder.toString()
            tfOrder[i].prompt = reorder.toString()
        }
        removeComponents(
            cbRotatable,
            startGameButton,
            randomizeButton,
            leaveButton,
            sessionID
        )
    }

    /**
     * is called when a new player joins the lobby - removes already initialized components, adds the new
     * player and then re-initialises all components again
     */
    override fun refreshAfterPlayerJoined(sender: String) {
        if (cableCarApplication.players.size >= 6) {
            cableCarApplication.players.add(Pair(sender, PlayerType.HUMAN))
            startGameButton.isDisabled = true
            return
        }
        if (!host) {
            cableCarApplication.players.add(Pair(sender, PlayerType.HUMAN))
            removeComponents()
            initPlayerNames(cableCarApplication.players)
        } else {
            cableCarApplication.players.add(Pair(sender, PlayerType.HUMAN))
            removeComponents()
            initLobbyMenu(cableCarApplication.players)
        }
    }

    /**
     * is called when a player leaves the lobby - either a player is removed from the players list or the whole
     * lobby is closed (when the host leaves the lobby)
     */
    override fun refreshAfterPlayerLeft(sender: String) {
        var closedGame = false
        removeComponents()
        for (i in 0 until cableCarApplication.players.size) {
            if (cableCarApplication.players[i].first == sender) {
                if (i == 0) {
                    cableCarApplication.rootService.networkService.disconnect()
                    cableCarApplication.players.clear()
                    cableCarApplication.joinMenu.clearScene()
                    cableCarApplication.showMenuScene(cableCarApplication.homeMenu)
                    closedGame = true
                } else {
                    cableCarApplication.players.removeAt(i)
                }
                break
            }
        }
        if (!closedGame) {
            if (!host) {
                initPlayerNames(cableCarApplication.players)
            } else {
                initLobbyMenu(cableCarApplication.players)
            }
        }
    }
}