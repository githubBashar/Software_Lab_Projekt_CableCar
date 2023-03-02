package view

import entity.Player
import entity.PlayerType
import service.CardImageLoader
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import javax.imageio.ImageIO
import entity.MainGame
import kotlin.math.min

/**
 * provides the [PlayerNameLabel] and its layout required by the GameScene.
 * It is inherited from the Pane and its size should be the length and width of the GameScene.
 *
 * @param posX X-Coordinate of this Pane
 * @param posY Y-Coordinate of this Pane
 * @param width the width of this Pane, and it should be the same value of [GameScene]
 * @param height the height of this Pane, and it should be the same value of [GameScene]
 */
class PlayerNameLabel(
    posX: Number = 0, posY: Number = 0,
    width: Number, height: Number
): Pane<Label>(
    posX = posX, posY = posY, width = width, height = height
) {
    private val basicPosX = width.toDouble()
    private val basicPosY = 650

    private val currentPlayerNameLabel = Label(
        posX = 20, posY = 1550,
        width = 464 * 2, height = 220,
        text = "Init fail",
        font = Font(size = 100, color = PlayerColor.getPlayerColorVisual(1)),
        alignment = Alignment.CENTER_LEFT
    ).apply {
        visual = ImageVisual(ImageIO.read(CardImageLoader::class.java.getResource("/human_alarming.png")))
    }

    private val next1PlayerNameLabel = Label(
        posX = (basicPosX - 20 - 464 * 2) , posY = basicPosY,
        width = 434, height = 110,
        text = "Init fail",
        font = Font(size = 50, color = PlayerColor.getPlayerColorVisual(2)),
        alignment = Alignment.CENTER_LEFT
    )

    private val next2PlayerNameLabel = Label(
        posX = (basicPosX - 20 - 464) , posY = basicPosY,
        width = 434, height = 110,
        text = "Init fail",
        font = Font(size = 50, color = PlayerColor.getPlayerColorVisual(3)),
        alignment = Alignment.CENTER_LEFT
    )

    private val next3PlayerNameLabel = Label(
        posX = (basicPosX - 20 - 464 * 2) , posY = basicPosY + 464,
        width = 434, height = 110,
        text = "Init fail",
        font = Font(size = 50, color = PlayerColor.getPlayerColorVisual(4)),
        alignment = Alignment.CENTER_LEFT
    )

    private val next4PlayerNameLabel = Label(
        posX = (basicPosX - 20 - 464) , posY = basicPosY + 464,
        width = 434, height = 110,
        text = "Init fail",
        font = Font(size = 50, color = PlayerColor.getPlayerColorVisual(5)),
        alignment = Alignment.CENTER_LEFT
    )

    private val next5PlayerNameLabel = Label(
        posX = (basicPosX - 20 - 464 * 2) , posY = basicPosY + 464 * 2,
        width = 434, height = 110,
        text = "Init fail",
        font = Font(size = 50, color = PlayerColor.getPlayerColorVisual(6)),
        alignment = Alignment.CENTER_LEFT
    )

    private val nameLabelList = mutableListOf(
        currentPlayerNameLabel,
        next1PlayerNameLabel,
        next2PlayerNameLabel,
        next3PlayerNameLabel,
        next4PlayerNameLabel,
        next5PlayerNameLabel
    )

    val backgroundLight = "-fx-background-color: rgba(0, 0, 0, 0.1);"
    val backGroundDark = "-fx-background-color: rgba(0, 0, 0, 0.3);"

    private fun updateLabelBackground(mainPlayer: Int, players: MutableList<Player>) {
        for (i in nameLabelList.indices) {
            val playerIndex = mainPlayer + i % players.size
            val target = nameLabelList[i]
            val radius = min(target.width, target.height) / 8
            target.backgroundStyle = if (playerIndex == 5) {
                backGroundDark
            }
            else {
                backgroundLight
            }
            target.backgroundStyle += "-fx-background-radius: ${radius}px;"
        }
    }

    init {
        this.addAll(
            currentPlayerNameLabel,
            next1PlayerNameLabel,
            next2PlayerNameLabel,
            next3PlayerNameLabel,
            next4PlayerNameLabel,
            next5PlayerNameLabel
        )
    }

    /**
     * How to play locally or over network
     */
    enum class GameType {
        NETWORKGAME,
        LOCALGAME
    }

    /**
     * This method will generate the text and Visual information required for the specified Label.
     *
     * @param name the player name for the Label
     * @param playerType the [PlayerType] for the Label
     * @param gameType the [GameType] of this Game
     * @param isCurrentPlayer Indicate if this player is a player in the current round of play
     * @param score the current score of the player
     *
     * @return is a [Pair], [Pair.first] is the [String] should filled in the [Label.text],
     * [Pair.second] is the [ImageVisual] should filled in the [Label.visual]
     */
    private fun labelConfigurator(
        name: String, playerType: PlayerType, gameType: GameType, isCurrentPlayer: Boolean, score: Int
    ): Pair<String, ImageVisual> {
        val imageVisual: ImageVisual
        if (gameType == GameType.LOCALGAME) {
            imageVisual = if (playerType == PlayerType.HUMAN) {
                ImageVisual(ImageIO.read(CardImageLoader::class.java.getResource("/human.png")))
            } else {
                ImageVisual(ImageIO.read(CardImageLoader::class.java.getResource("/roboter.png")))
            }
        }
        else {
            imageVisual = if (playerType == PlayerType.HUMAN) {
                if (isCurrentPlayer) {
                    ImageVisual(ImageIO.read(CardImageLoader::class.java.getResource("/human_alarming.png")))
                }
                else {
                    ImageVisual(ImageIO.read(CardImageLoader::class.java.getResource("/human.png")))
                }
            }
            else {
                if (isCurrentPlayer) {
                    ImageVisual(ImageIO.read(CardImageLoader::class.java.getResource("/roboter_alarming.png")))
                }
                else {
                    ImageVisual(ImageIO.read(CardImageLoader::class.java.getResource("/roboter.png")))
                }
            }
        }
        return if (score == 1) {
            Pair(
                "     $name:\n     1 Point",
                imageVisual
            )
        }
        else {
            Pair(
                "     $name:\n     $score Points",
                imageVisual
            )
        }
    }

    /**
     * This method configures the visibility of the individual NameLabel in the current game。
     * It should be called in [GameScene.refreshAfterGameStart()]
     *
     * @param players is the player list of current game, Normally it would be [MainGame.players]
     *
     * @throws IllegalArgumentException if the [players] have more than 6 element, because the maximum
     * player number of one game is 6
     */
    fun updateAfterStartGame(players: MutableList<Player>) {
        val size = players.size
        if (size > 6) throw IllegalArgumentException("Too many players")
        for (i in nameLabelList.indices) {
            nameLabelList[i].isVisible = i < size
        }
    }

    /**
     * update the name label after a local game turn end.
     */
    fun updateAfterTurnEndLocal(players: MutableList<Player>, currentPlayer: Int) {
        val size = players.size
        if (size > 6) throw IllegalArgumentException("Too many players")
        var index = currentPlayer
        for (i in nameLabelList.indices) {
            if (nameLabelList[i].isVisible) {
                val player = players[index]
                val labelConfig = labelConfigurator(
                    player.name, player.playerType,
                    GameType.LOCALGAME, false, player.points
                )

                nameLabelList[i].text = labelConfig.first
                nameLabelList[i].visual = labelConfig.second

                nameLabelList[i].font = Font(
                    size = nameLabelList[i].font.size,
                    color = PlayerColor.getPlayerColorVisual(index + 1)
                )
                index++
                index %= size
            }
        }

        updateLabelBackground(currentPlayer, players)
    }

    /**
     * update the name label after a network game turn end.
     */
    fun updateAfterTurnEndNetwork(players: MutableList<Player>, currentPlayer: Int, localPlayer: Int) {
        val size = players.size
        if (size > 6) throw IllegalArgumentException("Too many players")
        for (i in players.indices) {
            val index = (i + localPlayer) % size
            if (nameLabelList[i].isVisible) {
                val player = players[index]
                val labelConfig = if (index == currentPlayer) {
                    labelConfigurator(
                        player.name, player.playerType,
                        GameType.NETWORKGAME, true, player.points
                    )
                }
                else {
                    labelConfigurator(
                        player.name, player.playerType,
                        GameType.NETWORKGAME, false, player.points
                    )
                }
                nameLabelList[i].text = labelConfig.first
                nameLabelList[i].visual = labelConfig.second
                nameLabelList[i].font = Font(
                    size = nameLabelList[i].font.size,
                    color = PlayerColor.getPlayerColorVisual(index + 1)
                )
            }
        }

        updateLabelBackground(localPlayer, players)
    }
}