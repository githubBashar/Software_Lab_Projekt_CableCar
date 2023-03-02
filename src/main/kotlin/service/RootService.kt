package service
import entity.MainGame
import view.Refreshable

/**
 * The RootService is the heart of the program.
 * All information is retrieved and forwarded via it
 *
 * @property gameService The connection to the [GameService]
 * @property playerService The connection to [PlayerService]
 * @property undoRedoService The connection to [UndoRedoService]
 * @property scoreService The connection to the [ScoreService]
 * @property aiService The connection to [AiService]
 * @property networkService The connection to the [NetworkService]
 */
class RootService {
    /**
     * The current game. Can be "null" if it has not started yet
     */
    val gameService = GameService(this)
    val playerService = PlayerService(this)
    val undoRedoService = UndoRedoService(this)
    val scoreService = ScoreService(this)
    val legalService = LegalService(this)
    val networkService = NetworkService(this)
    var mainGame: MainGame? = null
    val aiService = AiService(this)

    /**
     * this method adds the [refreshable]
     */
    fun addRefreshable(newRefreshable: Refreshable) {
        gameService.addRefreshable(newRefreshable)
        playerService.addRefreshable(newRefreshable)
        undoRedoService.addRefreshable(newRefreshable)
        aiService.addRefreshable(newRefreshable)
        networkService.addRefreshable(newRefreshable)
    }

    /**
     * Ignites the [Refreshable]
     *
     * @param method The function to be executed
     */
    fun addRefreshables(vararg newRefreshables: Refreshable) {
        newRefreshables.forEach { addRefreshable(it) }
    }
}
