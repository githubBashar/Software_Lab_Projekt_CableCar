package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color
/**
 * [HelpMenu] is a button on the gams scene
 * description of the game in text
 */
class HelpMenu : MenuScene(900,900,
    background = ColorVisual(Color.WHITE)) {
    private val title  = tools.aqua.bgw.components.uicomponents.TextArea(
        posX = 10, posY = 20,
        width = 890, height = 770,text = "Game process of Cable Car:\n" +
                "The map – it shows:\n" +
                "• 60 square spaces, on which the track tiles are placed,\n" +
                "• 32 numbered cable car stations at the edge of the map,\n" +
                "• 1 „power station“ with 8 connections in the center of the map,\n" +
                "• 1 scoring track running along the edge of the map.\n" +
                "  6 scoring markers – One scoring marker each in the 6 player\n" +
                " colors, used for counting victory points. \n" +
                "• 60 tiles as a draw pile\n" +
                "• 61 Cable-Car-Waggons – in the 6 player colors yellow, blue,\n" +
                " orange, green, purple, and black.\n"+
                " Each player takes all cars of their selected color\n" +
                " and places them on the map, according to the\n" +
                " charts shown below and to the right. The numbers\n" +
                " indicate the spaces where the players have to place their cars.\n" +
                " All cars must be placed on the right track (dead end) of the\n" +
                " Cable Car Stations, where they remain until the end of the\n" +
                " game. The left tracks (little station building) do not belong to\n" +
                " any players. Any unused cars are returned to the box, they are\n" +
                " not used in this game.\n\n"+
                "Sequence of play: \n" +
                "Action: Place track tiles\n" +
                "The active player (the player performing their turn) places their\n" +
                "hand held tile on the map. If they don’t want to place this tile\n" +
                "they may draw a new tile from the face down supply, as long as\n" +
                "there are any tiles left. In this case they must place the tile just\n" +
                "drawn, keeping their hand tile.\n" +
                "After placing their hand tile the player draws a new tile form the\n" +
                "face down supply, as long as there are any tiles left.\n" +
                "A player who has connected all their lines to destination stations\n" +
                "still keeps on placing tiles.\n" +
                "Note: The track tiles have been designed in such a way that all lines are\n" +
                "connecting to some station at the end of the game. It may happen though\n" +
                "that loops are constructed which do not connect to any station. Such loops\n" +
                "are of no relevance to the game.\n\n " +
                "Scoring and end of the game: \n" +
                "A scoring takes place as soon as a cable car line has connected\n" +
                "to a destination station.\n" +
                "The owner of this line immediately gains 1 victory point for\n" +
                "each tile their line is passing. If the same tile is passed more than\n" +
                "once by this line than it generates 1 victory point each time it is\n" +
                "passed. If the line is connecting to the power station in the center\n" +
                "of the map the victory points total for this line is doubled.\n" +
                "Each player’s victory points are recorded on the scoring track\n" +
                "using the player’s scoring marker.\n" +
                "The car is turned by 90° indicating that this line has been scored.\n" +
                "Note: The tracks of the power-station printed on the map do not count for\n" +
                "victory points when a line connects to it. Only those tiles actually placed on\n" +
                "the map by the players count for victory points.\n" +
                "The game ends as soon as all lines have been scored and all tiles\n" +
                "have been laid out. The player with the most victory points is\n" +
                "the winner of the game.\n\n" +

                "This game is a project of the module (SoPra) at the TU Dortmund:\n" +
                "https://sopra.cs.tu-dortmund.de/wiki/sopra/22d/projekt2" ,
        font = Font(size =25, family = "ItaLIC")
    )

    val continueGameButton: Button = Button(
        height = 80,
        width = 160,
        posX = 365,
        posY = 800,
        text = "Continue",
        font = Font(20,color = Color.BLACK, fontStyle = Font.FontStyle.ITALIC),
        visual = ColorVisual(139,69,19)
    )

    init {
        addComponents(
            continueGameButton, title
        )
    }
}