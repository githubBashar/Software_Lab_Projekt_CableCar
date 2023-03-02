package view

import entity.MainGame
import entity.PlayerType
import service.CardImageLoader
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import javax.imageio.ImageIO

/**
 * This class is the AI-Speed control bar
 *
 * @property slowSpeed is the slowest simulation speed of AI
 * @property middleSpeed is the normal simulation speed of AI
 * @property fastSpeed is the fastest simulation speed of AI
 */
class SimulateSpeed(posX: Number = 4400, posY: Number = 340): Pane<Label>(
    posX = posX, posY = posY,
    width = 980, height = 210
) {
    // 1:1.4
    private val slowSpeed = 4.0
    private val middleSpeed = 2.5
    private val fastSpeed = 1.0

    var mainGame: MainGame? = null

    private val title = Label(
        posX = 0, posY = -75, width = width,
        height = 75, text = "AI Simulation Speed: ", font = Font(size = 75)
    )

    /**
     * the speed of the AI
     */
    enum class Speed{
        SLOW,
        MIDDLE,
        FAST
    }

     var speedMode = Speed.MIDDLE
        set(value) {
            field = value
            pos.forEach {
                it.backgroundStyle = notSelectedAppearance
            }
            when (value) {
                Speed.SLOW -> posSlow.backgroundStyle = selectedAppearance
                Speed.MIDDLE -> posMiddle.backgroundStyle = selectedAppearance
                Speed.FAST -> posFast.backgroundStyle = selectedAppearance
            }
        }

    private val radius = 50

    private val selectedAppearance = "-fx-background-color: rgba(255, 255, 255, 255);" +
    "-fx-background-radius: ${radius}px;"

    private val notSelectedAppearance = "-fx-background-color: rgba(255, 255, 255, 0);" +
            "-fx-background-radius: ${radius}px;"

    private val board = Label(width = super.width, height = super.height).apply {
        visual = ImageVisual(ImageIO.read(CardImageLoader::class.java.getResource("/set_speed.png")))
    }

    private val posSlow = Label(
        posX = 170, posY = 55, width = 100, height = 100, visual = ColorVisual.TRANSPARENT
    ).apply {
        componentStyle = "-fx-background-radius: ${radius}px"
        backgroundStyle = notSelectedAppearance
        onMouseClicked = {
            speedMode = Speed.SLOW
            if (mainGame != null) {
                mainGame!!.simulationVit = slowSpeed
            }
        }
        /*onDragDropped = {
            speedMode = Speed.SLOW
            if (mainGame != null) {
                mainGame.simulationVit = 1.0
            }
        }*/
    }

    private val posMiddle = Label(
        posX = 440, posY = 55, width = 100, height = 100, visual = ColorVisual.TRANSPARENT
    ).apply {
        componentStyle = "-fx-background-radius: ${radius}px"
        backgroundStyle = notSelectedAppearance
        onMouseClicked = {
            speedMode = Speed.MIDDLE
            if (mainGame != null) {
                mainGame!!.simulationVit = middleSpeed
            }
        }
        /*onDragDropped = {
            speedMode = Speed.MIDDLE
            if (mainGame != null) {
                mainGame.simulationVit = 2.0
            }
        }*/
    }

    private val posFast = Label(
        posX = 710, posY = 55, width = 100, height = 100, visual = ColorVisual.TRANSPARENT
    ).apply {
        componentStyle = "-fx-background-radius: ${radius}px"
        backgroundStyle = notSelectedAppearance
        onMouseClicked = {
            speedMode = Speed.FAST
            if (mainGame != null) {
                mainGame!!.simulationVit = fastSpeed
            }
        }
        /*onDragDropped = {
            speedMode = Speed.FAST
            if (mainGame != null) {
                mainGame.simulationVit = 3.0
            }
        }*/
    }

    private val pos = mutableListOf(posSlow, posMiddle, posFast)
    init {
        this.addAll(
            title,
            board,
            posSlow,
            posMiddle,
            posFast
        )
    }

    /**
     * init the component of the class at the beginning of a game
     */
    fun init(mainGame: MainGame?) {
        this.mainGame = mainGame
        this.isVisible = false
        mainGame!!.players.forEach {
            if (it.playerType != PlayerType.HUMAN) this.isVisible = true
        }
        mainGame.simulationVit = middleSpeed
        speedMode = Speed.MIDDLE
    }
}