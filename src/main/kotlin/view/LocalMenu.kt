package view

import entity.PlayerType
import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.dialog.Dialog
import tools.aqua.bgw.dialog.DialogType
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * displayed when the user presses "Play locally" at [HomeMenu],
 * each player has to fill in a name as well as a player type.
 * @param cableCarApplication is an object of class [CableCarApplication]
 */
class LocalMenu(private val cableCarApplication: CableCarApplication) : MenuScene(1160, 900), Refreshable {

    /**
     * error-dialogs if there are less than two players or some players have the same name
     */
    val sameName: Dialog = Dialog(
        DialogType.ERROR,
        title = "Same Name",
        header = "Correction required",
        message = "You have to enter different names!"
    )

    val noPlayerType: Dialog = Dialog(
        DialogType.ERROR,
        title = "No Player Type",
        header = "Correction required",
        message = "You have to enter a player type for every player!"
    )

    /**
     * all necessary text fields
     */
    private val p1Input: TextField = TextField(
        posX = 160, posY = 240, prompt = "", width = 250, height = 50, font = Font(size = 30, Color(210, 105, 30))
    ).apply {
        onKeyTyped = {
            goToLobbyButton.isDisabled = disableButton()
        }
    }

    private val p3Input: TextField = TextField(
        posX = 160,
        posY = 440,
        prompt = "optional",
        width = 250,
        height = 50,
        font = Font(size = 30, Color(210, 105, 30))
    ).apply {
        onKeyTyped = {
            goToLobbyButton.isDisabled = disableButton()
        }
    }

    private val p5Input: TextField = TextField(
        posX = 160, posY = 640, prompt = "optional", width = 250, height = 50,
        font = Font(size = 30, Color(210, 105, 30)),
    ).apply {
        onKeyTyped = {
            goToLobbyButton.isDisabled = disableButton()
        }
    }

    private val p2Input: TextField = TextField(
        posX = 750, posY = 240, prompt = "", width = 250, height = 50,
        font = Font(size = 30, Color(210, 105, 30)),
    ).apply {
        onKeyTyped = {
            goToLobbyButton.isDisabled = disableButton()
        }
    }

    private val p4Input: TextField = TextField(
        posX = 750, posY = 440, prompt = "optional", width = 250, height = 50,
        font = Font(size = 30, Color(210, 105, 30)),
    ).apply {
        onKeyTyped = {
            goToLobbyButton.isDisabled = disableButton()
        }
    }

    private val p6Input: TextField = TextField(
        posX = 750, posY = 640, prompt = "optional", width = 250, height = 50,
        font = Font(size = 30, Color(210, 105, 30)),
    ).apply {
        onKeyTyped = {
            goToLobbyButton.isDisabled = disableButton()
        }
    }


    /**
     * list of Text fields for input of player names
     */
    private var tfPlayerNames = mutableListOf(
        p1Input, p2Input, p3Input, p4Input, p5Input, p6Input
    )

    /**
     * Combo boxes to select the player type
     */
    private var p1ComboBox = ComboBox<String>(
        posX = 185, posY = 295, width = 200, prompt = "Select the Player Type!"
    )
    private var p2ComboBox = ComboBox<String>(
        posX = 785, posY = 295, width = 200, prompt = "Select the Player Type!"
    )
    private var p3ComboBox = ComboBox<String>(
        posX = 185, posY = 495, width = 200, prompt = "Select the Player Type!"
    )
    private var p4ComboBox = ComboBox<String>(
        posX = 785, posY = 495, width = 200, prompt = "Select the Player Type!"
    )
    private var p5ComboBox = ComboBox<String>(
        posX = 185, posY = 695, width = 200, prompt = "Select the Player Type!"
    )
    private var p6ComboBox = ComboBox<String>(
        posX = 785, posY = 695, width = 200, prompt = "Select the Player Type!"
    )


    /**
     * MutableList of ComboBoxes to retrieve what PlayerType a Player is
     */
    private val cbPlayers = mutableListOf(
        p1ComboBox, p2ComboBox, p3ComboBox, p4ComboBox, p5ComboBox, p6ComboBox
    )

    /**
     * strings to save player names and player types
     */
    private var playerNames = mutableListOf("", "", "", "", "", "")
    private var playerTypes = mutableListOf("", "", "", "", "", "")

    /**
     * all Buttons
     */
    val goToLobbyButton = GameButton(
        200, 70, 100, 770, "Go to Lobby", Font(size = 25, fontWeight = Font.FontWeight.BOLD), ColorVisual(144, 240, 142)
    )

    private val homeMenuButton = GameButton(
        200, 70, 860, 770, "Home Menu", Font(size = 25, fontWeight = Font.FontWeight.BOLD), ColorVisual(137, 4, 6)
    ).apply {
        onMouseClicked = {
            clearScene()
            cableCarApplication.hostMenu.clearScene()
            cableCarApplication.joinMenu.clearScene()
            cableCarApplication.showMenuScene(cableCarApplication.homeMenu)
        }
    }

    /**
     * init for localMenu
     */
    init {
        for (i in 0..5) {
            //adding the ComboBox Items for each ComboBox
            cbPlayers[i].items = mutableListOf("Player", "Easy AI", "Hard AI", "Select the Player Type!")
            addComponents(tfPlayerNames[i], cbPlayers[i])
            //adding listeners to each TextField and ComboBox to later retrieve the inputs
            tfPlayerNames[i].textProperty.addListener { _, input -> playerNames[i] = input }
            cbPlayers[i].selectedItemProperty.addListener { _, input -> playerTypes[i] = input!! }
        }
        //for less than 2 players this button is disabled
        goToLobbyButton.isDisabled = true

        background = ImageVisual("playlocally.jpg")
        opacity = 1.0
        addComponents(
            homeMenuButton, goToLobbyButton
        )
    }

    /**
     * helper function to determine if two players have the same name
     */
    fun checkForSameName(): Boolean {
        for (i in 0 until playerNames.size) {
            for (j in 0 until playerNames.size) {
                if (playerNames[i] != "" && i != j && playerNames[i] == playerNames[j]) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * helper function to determine if for every player there is a set player type
     * - it does not matter if the player type is set but the name is not => the player will not be instantiated
     */
    fun checkForPlayerType(): Boolean {
        for (i in 0 until playerTypes.size) {
            if (playerNames[i] != "" && (playerTypes[i] == "" || playerTypes[i] == "-")) {
                return false
            }
        }
        return true
    }

    /**
     * helper function to create list of players with its corresponding player type
     */
    fun createPlayerList(): MutableList<Pair<String, PlayerType>> {
        val players = mutableListOf<Pair<String, PlayerType>>()

        for (i in 0 until playerNames.size) {
            if (playerNames[i] != "") {
                players.add(Pair(playerNames[i], cableCarApplication.stringToPlayerType(playerTypes[i])!!))
            }
        }
        return players
    }


    /**
     * helper function to clear the scene of every input
     */
    fun clearScene() {
        for (i in 0..5) {
            tfPlayerNames[i].text = ""
            cbPlayers[i].selectedItem = "Select the Player Type!"
            playerNames[i] = ""
            playerTypes[i] = ""
        }
        goToLobbyButton.isDisabled = true
    }

    /**
     * helper function that determines if the game has at least two player => [goToLobbyButton] is going to be enabled
     */
    private fun disableButton(): Boolean {
        var counter = 0
        for (name in playerNames) {
            if (name != "") {
                counter++
            }
        }
        return counter < 2
    }
}