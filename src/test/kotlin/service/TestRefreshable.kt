package service

import view.Refreshable

/**
 * Test cases for the [Refreshable] class
 */
class TestRefreshable : Refreshable {

    var refreshAfterHostGame: Boolean = false
        private set

    var refreshAfterPlayerJoined: Boolean = false
        private set

    var refreshAfterStartGame: Boolean = false
        private set

    var refreshAfterPlayerLeft = false
        private  set

    var refreshAfterError = false
        private set
    var refreshAfterRotate = false
        private set

    var refreshAfterJoinGame: Boolean = false
        private set
    var refreshAfterEndGame: Boolean = false
        private set

    var refreshAfterTurnEnds: Boolean = false
        private set

    var refreshAfterUndo: Boolean = false
        private set

    var refreshAfterRedo: Boolean = false
        private set

    var refreshAfterDraw: Boolean = false
        private set


    /**
     * resets all *Called properties to false
     */
    fun reset() {
        refreshAfterStartGame = false
        refreshAfterEndGame = false
        refreshAfterTurnEnds = false
        refreshAfterUndo = false
        refreshAfterRedo = false
        refreshAfterDraw = false
        refreshAfterHostGame = false
        refreshAfterJoinGame = false
        refreshAfterPlayerJoined = false
        refreshAfterRotate = false
        refreshAfterPlayerLeft = false
        refreshAfterError = false
    }

    override fun refreshAfterStartGame() {
        refreshAfterStartGame = true
    }

    override fun refreshAfterEndGame() {
        refreshAfterEndGame = true
    }

    override fun refreshAfterTurnEnds() {
        refreshAfterTurnEnds = true
    }

    override fun refreshAfterUndo() {
        refreshAfterUndo = true
    }

    override fun refreshAfterRedo() {
        refreshAfterRedo = true
    }

    override fun refreshAfterDraw() {
        refreshAfterDraw = true
    }

    override fun refreshAfterHostGame() {
        refreshAfterHostGame = true
    }

    override fun refreshAfterJoinGame(opponents: List<String>) {
        refreshAfterJoinGame = true
    }

    override fun refreshAfterPlayerJoined(sender: String) {
        refreshAfterPlayerJoined = true
    }

    override fun refreshAfterRotate() {
        refreshAfterRotate = true
    }

    override fun refreshAfterPlayerLeft(sender: String) {
        refreshAfterPlayerLeft = true
    }

    override fun refreshAfterError(msg: String) {
        refreshAfterError = true
    }
}
