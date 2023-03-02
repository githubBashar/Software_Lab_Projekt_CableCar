package view


import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual


/**
 * [MenuScene] used to start a new game. It will be displayed or reached directly when the program starts, or
 * when "new game" is clicked in [EndScene] or "go home menu" is clicked in [GameScene].
 * It contains 4 buttons, [Play locally], [Join Game], [Host Game] and [ Quit ]
 * @param cableCarApplication Reference to Cable Car game
 */

class HomeMenu(private val cableCarApplication: CableCarApplication) : MenuScene(1160, 900),
    Refreshable {
    private val localButton = GameButton(
        200, 100, 500, 260, "Play locally",
        Font(size = 25, fontWeight = Font.FontWeight.BOLD), ColorVisual(155, 100, 6)
    ).apply {
        onMouseClicked = {
            cableCarApplication.showMenuScene(cableCarApplication.localMenu)
        }
    }

    private val joinButton = GameButton(
        200, 100, 500, 380, "Join Game",
        Font(size = 25, fontWeight = Font.FontWeight.BOLD), ColorVisual(155, 100, 6)
    ).apply {
        onMouseClicked = {
            cableCarApplication.showMenuScene(cableCarApplication.joinMenu)
        }
    }

    private val hostButton = GameButton(
        200, 100, 500, 500, "Host Game",
        Font(size = 25, fontWeight = Font.FontWeight.BOLD), ColorVisual(155, 100, 6)
    ).apply {
        onMouseClicked = {
            cableCarApplication.showMenuScene(cableCarApplication.hostMenu)
        }
    }

    val quitButton = GameButton(
        200, 100, 500, 750, "Quit",
        Font(size = 25, fontWeight = Font.FontWeight.BOLD), ColorVisual(137, 4, 6)
    ).apply {
        onMouseClicked = {
            cableCarApplication.exit()
        }
    }

    init {
        apply {
            background = ImageVisual("sfcc.jpg")
            opacity = 1.0
        }
        addComponents(
            hostButton,
            joinButton,
            localButton,
            quitButton
        )
    }
}

