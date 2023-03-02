package view

import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * is a button on the [GameScene].
 * there are 3 buttons in it, one for continue from game, one for quit application
 * and one for displaying home menu
 */
class OverlayMenu(cableCarApplication: CableCarApplication) : MenuScene(
    900, 900,
    background = ColorVisual(Color.WHITE)
) {
    val exitButton = GameButton(
        300, 150, 300, 320, "Exit Application",
        font = Font(32, color = Color.BLACK, fontStyle = Font.FontStyle.ITALIC), ColorVisual(155, 100, 6)
    )

    val continueGameButton = GameButton(
        300, 150, 300, 490, "Continue",
        font = Font(32, color = Color.BLACK, fontStyle = Font.FontStyle.ITALIC), ColorVisual(155, 100, 6)
    )

    val homeButton = GameButton(
        300, 150, 300, 660, "Home Menu",
        font = Font(32, color = Color.BLACK, fontStyle = Font.FontStyle.ITALIC), ColorVisual(137, 4, 6)
    ).apply {
        onMouseClicked = {
            cableCarApplication.localMenu.clearScene()
            cableCarApplication.hostMenu.clearScene()
            cableCarApplication.joinMenu.clearScene()
            cableCarApplication.showMenuScene(cableCarApplication.homeMenu)
        }
    }
    init {
        background = ImageVisual("gamemenu.jpg")
        opacity = 1.0
        addComponents(
            exitButton, continueGameButton, homeButton
        )
    }
}