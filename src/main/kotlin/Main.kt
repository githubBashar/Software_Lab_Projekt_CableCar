import view.*

/**
 * Ist die Main-Klasse, die das Spiel startet
 */
fun main() {
    val cca = CableCarApplication()
    cca.show()
    cca.rootService.networkService.disconnect()
    println("Application ended. Goodbye")
}