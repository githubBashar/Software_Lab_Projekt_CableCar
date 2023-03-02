package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * [MenuScene], which is displayed when the game is finished. There are also three buttons: one to start a new game,
 * one to play the current game again and one to exit the programme.
 */
class EndScene :
    MenuScene(900, 900), Refreshable {

    val namePlayer1 = Label(
        width = 200, height = 300,
        posX = 450, posY = 107,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    val namePlayer2 = Label(
        width = 250, height = 300,
        posX = 200, posY = 170,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    val namePlayer3 = Label(
        width = 250, height = 300,
        posX = 200, posY = 270,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    val namePlayer4 = Label(
        width = 250, height = 300,
        posX = 200, posY = 370,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    val namePlayer5 = Label(
        width = 250, height = 300,
        posX = 200, posY = 470,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    val namePlayer6 = Label(
        width = 250, height = 300,
        posX = 200, posY = 570,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    //label that shows a winner if he/she exists
    val gameResult1 = Label(
        width = 200, height = 300,
        posX = 650, posY = 107,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    val gameResult2 = Label(
        width = 200, height = 300,
        posX = 450, posY = 170,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    val gameResult3 = Label(
        width = 500, height = 300,
        posX = 450, posY = 270,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    val gameResult4 = Label(
        width = 500, height = 300,
        posX = 450, posY = 370,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    val gameResult5 = Label(
        width = 500, height = 300,
        posX = 450, posY = 470,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    val gameResult6 = Label(
        width = 500, height = 300,
        posX = 450, posY = 570,
        text = "", font = Font(35, Color.white),
        alignment = Alignment.CENTER_LEFT
    )

    val quitButton = Button(
        width = 200, height = 100,
        posX = 350, posY = 750, text = "Quit", font = Font(24)
    ).apply {
        visual = ColorVisual(89, 0, 0)
    }

    init {
        opacity = 1.0
        addComponents(
            namePlayer1,
            namePlayer2,
            namePlayer3,
            namePlayer4,
            namePlayer5,
            namePlayer6,
            gameResult1,
            gameResult2,
            gameResult3,
            gameResult4,
            gameResult5,
            gameResult6,
            quitButton
        )
        background = ImageVisual("endscene.jpg")
    }

}