package entity

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * test class [CardType]
 */
class CardTypeTest {
    private var target = CardType.POWER_STATION

    /**
     * process the test
     */
    @Test
    fun test() {
        for (i in CardType.values()) {
            target = i
            assertEquals(i, target)
        }
    }
}