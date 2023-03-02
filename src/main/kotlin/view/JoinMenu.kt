package view

import entity.PlayerType
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.dialog.Dialog
import tools.aqua.bgw.dialog.DialogType
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

/**
 * Displayed when the user clicks the "Join Game" button on [HomeMenu].
 * This is the "Join Game" menu where the player or AI can join a network game.
 * @param cableCarApplication is an object of class [CableCarApplication]
 */
class JoinMenu(private val cableCarApplication: CableCarApplication) : MenuScene(1160, 900), Refreshable {

    /**
     * error-dialogs if inputs are missing, inputs are faulty or the game can not be found
     */
    val missingInputs: Dialog = Dialog(
        DialogType.ERROR,
        title = "Missing Inputs",
        header = "Correction required",
        message = "You have to enter a Session ID, Nickname and a player type!"
    )

    /**
     * all TextFields
     */
    private var tfSessionID = TextField(posX = 510, posY = 190, prompt = "", width = 350, height = 50)
    private var tfNickname = TextField(posX = 510, posY = 275, prompt = "", width = 350, height = 50)

    /**
     * all Buttons
     */

    val joinGameButton = GameButton(
        200, 70, 100, 770, "Join Game",
        Font(size = 25, fontWeight = Font.FontWeight.BOLD), ColorVisual(144, 240, 142)
    ).apply {
        onMouseClicked = {
            cableCarApplication.showMenuScene(cableCarApplication.lobbyMenu)
        }
    }

    private val homeMenuButton = GameButton(
        200, 70, 860, 770, "Home Menu",
        Font(size = 25, fontWeight = Font.FontWeight.BOLD), ColorVisual(137, 4, 6)
    ).apply {
        onMouseClicked = {
            clearScene()
            cableCarApplication.showMenuScene(cableCarApplication.homeMenu)
        }
    }

    /**
     * combobox to choose the type of player
     */
    private var pComboBox = ComboBox<String>(
        posX = 595, posY = 340, width = 200, prompt = "Select the Player Type!"
    )

    /**
     * strings where the inputs are saved
     */
    private var sessionID = ""
    private var nickname = ""
    private var playerType = ""

    init {
        pComboBox.items = mutableListOf("Player", "Easy AI", "Hard AI", "Select the Player Type!")
        pComboBox.selectedItemProperty.addListener { _, input -> playerType = input!! }
        background = ImageVisual("joingame.jpg")
        opacity = 1.0
        addComponents(
            pComboBox,
            tfSessionID,
            tfNickname,
            joinGameButton,
            homeMenuButton,
        )

        tfSessionID.textProperty.addListener { _, input -> sessionID = input }
        tfNickname.textProperty.addListener { _, input -> nickname = input }
    }

    /**
     * helper function to determine if all necessary information is given
     */
    fun missingInputs(): Boolean {
        return sessionID == "" || nickname == "" || playerType == "" || playerType == "Select the Player Type!"
    }

    /**
     * helper function to clear the scene of every input
     */
    fun clearScene() {
        tfSessionID.text = ""
        sessionID = ""
        tfNickname.text = ""
        nickname = ""
        pComboBox.selectedItem = "Select the Player Type!"
        playerType = "Select the Player Type!"
    }

    /**
     * helper function to have easy access to the text fields of this class
     */
    fun retrievePlayerInputs(): MutableList<String> {
        return mutableListOf(nickname, sessionID, playerType)
    }

    /**
     * helper function to create players out of the opponents list
     */
    private fun stringToPlayer(opponents: List<String>): MutableList<Pair<String, PlayerType>> {
        val players = mutableListOf<Pair<String, PlayerType>>()
        for (element in opponents) {
            players.add(Pair(element, PlayerType.HUMAN))
        }
        return players
    }

    /**
     * helper function to get the sessionID - used in [CableCarApplication]
     */
    fun getSessionID(): String {
        return sessionID
    }

    /**
     * is being called when a player wants to join a lobby - opponents are all players except the one who wants to join
     * updates the players list and initializes the lobby menu only for the player who is joining
     */
    override fun refreshAfterJoinGame(opponents: List<String>) {
        cableCarApplication.lobbyMenu.background = ImageVisual("sfcc_lj.jpg")
        if (opponents.size >= 6) {
            cableCarApplication.players = stringToPlayer(opponents)
            cableCarApplication.players.add(Pair(nickname, cableCarApplication.stringToPlayerType(playerType)!!))
            val players = stringToPlayer(opponents)
            cableCarApplication.lobbyMenu.initPlayerNames(players)
            cableCarApplication.showMenuScene(cableCarApplication.lobbyMenu)
        } else {
            cableCarApplication.players = stringToPlayer(opponents)
            cableCarApplication.players.add(Pair(nickname, cableCarApplication.stringToPlayerType(playerType)!!))
            cableCarApplication.lobbyMenu.initPlayerNames(cableCarApplication.players)
            cableCarApplication.showMenuScene(cableCarApplication.lobbyMenu)
        }
    }

}