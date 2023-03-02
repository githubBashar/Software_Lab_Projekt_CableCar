package entity
import kotlin.test.*

/**
 * Test cases for the [PlayerType] class
 */
class PlayerTypeTest {
    /**
     * process the test
     */
    @Test
    fun testPlayerType() {
        var testVar : PlayerType

        for (i in PlayerType.values()) {
            testVar = i
            assertEquals(i, testVar)
        }
    }
}