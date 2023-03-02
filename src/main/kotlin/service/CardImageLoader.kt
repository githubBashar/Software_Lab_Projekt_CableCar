package service

import java.awt.image.BufferedImage
import javax.imageio.ImageIO



private const val IMG_HEIGHT = 271
private const val IMG_WIDTH = 271
/**
 * Provides access to the src/main/resources/card_deck.jpg file that contains all card images
 * in a raster.
 * 271x271 pixels.
 */
class CardImageLoader {

    private val playCardsImage : BufferedImage = ImageIO.read(CardImageLoader
    ::class.java.getResource("/card_deck.jpg"))
    private val emptyCardImage : BufferedImage = ImageIO.read(CardImageLoader
    ::class.java.getResource("/empty.jpg"))
    private val powerStationImage : BufferedImage = ImageIO.read(CardImageLoader
    ::class.java.getResource("/power_station.jpg"))

    /**
     * Provides the card image for the given [id]
     */
    fun frontImageFor(id:Int) =
        getImageByCoordinates(id)

    /**
     * Provides the back side image of the card deck
     */
    val backImage: BufferedImage get() = playCardsImage.getSubimage(
        2714-271,
        1893-271,
        271,
        271
    )

    /**
     * retrieves from the full raster image the corresponding sub-image
     * [id]
     *
     * @param id identify the taken image
     */
    private fun getImageByCoordinates (id:Int) : BufferedImage {
        when (id) {
            in 0..59 -> {
                val x = id % 10
                val y = id / 10
                return playCardsImage.getSubimage(
                    x * IMG_WIDTH,
                    y * IMG_HEIGHT,
                    IMG_WIDTH,
                    IMG_HEIGHT
                )
            }
            -1 -> {
                return emptyCardImage.getSubimage(
                    0 * IMG_WIDTH,
                    0 * IMG_HEIGHT,
                    IMG_WIDTH,
                    IMG_HEIGHT
                )
            }
            -2 -> {
                return powerStationImage.getSubimage(
                    0 * IMG_WIDTH,
                    0 * IMG_HEIGHT,
                    IMG_WIDTH,
                    IMG_HEIGHT
                )
            }
            -3 -> {
                return powerStationImage.getSubimage(
                    0 * IMG_WIDTH,
                    1* IMG_HEIGHT,
                    IMG_WIDTH,
                    IMG_HEIGHT
                )
            }
            -4 -> {
                return powerStationImage.getSubimage(
                    1 * IMG_WIDTH,
                    0 * IMG_HEIGHT,
                    IMG_WIDTH,
                    IMG_HEIGHT
                )
            }
            -5 -> {
                return powerStationImage.getSubimage(
                    1 * IMG_WIDTH,
                    1 * IMG_HEIGHT,
                    IMG_WIDTH,
                    IMG_HEIGHT
                )
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }



}
