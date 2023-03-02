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
 * displayed when "Host Game" is clicked on [HomeMenu].
 * A new network game can be hosted here.
 * @param cableCarApplication is an object of class [CableCarApplication]
 */
class HostMenu(private val cableCarApplication: CableCarApplication) : MenuScene(1160, 900),
    Refreshable {

    /**
     * error-dialog if inputs are missing
     */
    val missingInputs: Dialog = Dialog(
        DialogType.ERROR,
        title = "Missing Inputs",
        header = "Correction required",
        message = "You have to enter a Session ID, Nickname and a player type!"
    )

    /**
     * the only text field
     */
    private var tfSessionID = TextField(posX = 510, posY = 190, prompt = "", width = 350, height = 50)
    private var tfNickname = TextField(posX = 510, posY = 275, prompt = "", width = 350, height = 50)

    /**
     * all Buttons
     */
    val startHostingButton = GameButton(
        200, 70, 100, 770, "Start Hosting",
        Font(size = 25, fontWeight = Font.FontWeight.BOLD), ColorVisual(144, 240, 142)
    )

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
     * string in which the nickname is saved
     */
    private var sessionID = ""
    private var nickname = ""
    var playerType = ""

    init {
        pComboBox.items = mutableListOf("Player", "Easy AI", "Hard AI", "Select the Player Type!")
        pComboBox.selectedItemProperty.addListener { _, input -> playerType = input!! }
        background = ImageVisual("hostgame.jpg")
        opacity = 1.0
        addComponents(
            pComboBox,
            tfNickname,
            startHostingButton,
            homeMenuButton,
            tfSessionID
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
     * function to retrieve information of the textFields within this class
     */
    fun createPlayerList(): MutableList<Pair<String, PlayerType>> {
        return mutableListOf(Pair(nickname, cableCarApplication.stringToPlayerType(playerType)!!))
    }

    /**
     * helper function to get the sessionID - used in [CableCarApplication]
     */
    fun getSessionID(): String {
        return sessionID
    }

    /**
     * is called when a new game is being hosted, creates a new player, calls networkService.hostGame() and starts
     * a new game, shows the LobbyMenu
     */
    override fun refreshAfterHostGame() {
        cableCarApplication.lobbyMenu.background = ImageVisual("gamelobby.jpg")
        cableCarApplication.players.add(Pair(nickname, cableCarApplication.stringToPlayerType(playerType)!!))
        cableCarApplication.rootService.networkService.hostGame(
            name = nickname,
            sessionID = sessionID,
            secret = "cable22"
        )
        if (playerType == "Easy AI") {
            cableCarApplication.rootService.networkService.client!!.isEasyAI = true
        }
        else if (playerType == "Hard AI") {
            cableCarApplication.rootService.networkService.client!!.isHardAI = true
        }
        cableCarApplication.lobbyMenu.startGameButton.isDisabled = true

        cableCarApplication.showMenuScene(cableCarApplication.lobbyMenu)
    }
}