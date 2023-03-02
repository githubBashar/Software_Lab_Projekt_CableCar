package view

import service.CardImageLoader
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.Visual
import java.awt.Color
import javax.imageio.ImageIO

/**
 * Each player gets different object colors
 */
class PlayerColor private constructor() {
    companion object {
        /**
         * Function that returns a possible player color
         * @param index index of the player
         * @return [ColorVisual] depending on the given index
         *  */
        fun  getPlayerColorVisual(index:Int):Color {
            return when (index) {
                // BLACK
                1 -> Color(60, 59, 61)
                // BLUE
                2 -> Color(17, 111, 177)
                // GREEN
                3 -> Color(0, 144, 48)
                // ORANGE
                4 -> Color(226, 147, 31)
                // VIOLET
                5 -> Color(174, 47, 131)
                // YELLOW
                6 -> Color(234, 206, 2)
                // index == 7 means this train belongs to nobody
                7 -> ColorVisual.TRANSPARENT.color
                // other index is not permitted
                else -> throw IllegalArgumentException("Illegal Player Identifier: $index")
            }
        }

        /**
         * get the [ImageVisual] for the trains.
         */
        fun getTrainImage(index: Int): Pair<Visual, Visual> {
            return when (index) {
                1 -> Pair(ImageVisual(
                        image = ImageIO.read(
                            CardImageLoader::class.java.getResource("/trains/black_front.png")
                        )),
                    ImageVisual(
                        image = ImageIO.read(
                            CardImageLoader::class.java.getResource("/trains/black_back.png"))
                    ))

                2 -> Pair(ImageVisual(
                    image = ImageIO.read(
                        CardImageLoader::class.java.getResource("/trains/blue_front.png")
                    )),
                    ImageVisual(
                        image = ImageIO.read(
                            CardImageLoader::class.java.getResource("/trains/blue_back.png"))
                    ))

                3 -> Pair(ImageVisual(
                    image = ImageIO.read(
                        CardImageLoader::class.java.getResource("/trains/green_front.png")
                    )),
                    ImageVisual(
                        image = ImageIO.read(
                            CardImageLoader::class.java.getResource("/trains/green_back.png"))
                    ))

                4 -> Pair(ImageVisual(
                    image = ImageIO.read(
                        CardImageLoader::class.java.getResource("/trains/orange_front.png")
                    )),
                    ImageVisual(
                        image = ImageIO.read(
                            CardImageLoader::class.java.getResource("/trains/orange_back.png"))
                    ))

                5 -> Pair(ImageVisual(
                    image = ImageIO.read(
                        CardImageLoader::class.java.getResource("/trains/violett_front.png")
                    )),
                    ImageVisual(
                        image = ImageIO.read(
                            CardImageLoader::class.java.getResource("/trains/violett_back.png"))
                    ))

                6 -> Pair(ImageVisual(
                    image = ImageIO.read(
                        CardImageLoader::class.java.getResource("/trains/yellow_front.png")
                    )),
                    ImageVisual(
                        image = ImageIO.read(
                            CardImageLoader::class.java.getResource("/trains/yellow_back.png"))
                    ))

                7 -> Pair(ColorVisual.TRANSPARENT, ColorVisual.TRANSPARENT)

                else -> throw IllegalArgumentException("Illegal Player Identifier: $index")
            }
        }
    }

}