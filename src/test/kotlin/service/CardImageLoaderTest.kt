package service

import java.awt.image.BufferedImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


/**
 * Test cases for the [CardImageLoader]
 */
class CardImageLoaderTest {
    /**
     * The [CardImageLoader] that is tested with this test class
     */
    private val imageLoader: CardImageLoader = CardImageLoader()

    /**
     * Loads the image for every possible suit/value combination as well as
     * front and back side and checks whether the resulting [BufferedImage]
     * has the correct dimensions of 271x271 px.
     */
    @Test
    fun testLoadAll() {
        val allImages = mutableListOf<BufferedImage>()
        for (id in -5.. 59){
            allImages += imageLoader.frontImageFor(id)

        }
        allImages += imageLoader.backImage

        allImages.forEach {
            assertEquals(271, it.width)
            assertEquals(271, it.height)
        }
        assertFailsWith<IllegalStateException> { imageLoader.frontImageFor(77) }
    }
}
