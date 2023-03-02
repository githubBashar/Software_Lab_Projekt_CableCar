package view
import service.AbstractRefreshingService
/**
 * This interface provides a mechanism
 * with which the service classes can tell the view classes
 * that certain changes were made to the entity layer,
 * so that the surface is changed accordingly when used.
 * All the following methods are empty, so they are dependent on the implementing classes in the View layer
 * to be completed
 * @see AbstractRefreshingService
 */
interface Refreshable{
    /**
     * will be updated after start game
     */
    fun refreshAfterStartGame(){}

    /**
     * will be updated after end game
     */
    fun refreshAfterEndGame() {}

    /**
     * updated after each turn
     */
    fun refreshAfterTurnEnds(){}

    /**
     * is updated after the "UNDO" action
     */
    fun refreshAfterUndo(){}

    /**
     * is updated after the "REDO" action
     */
    fun refreshAfterRedo(){}

    /**
     * will be updated after the draw card action
     */
    fun refreshAfterDraw(){}

    /**
     * is updated after card rotation action
     */
    fun refreshAfterRotate(){}

    /**
     * will be updated after a game is hosted
     */
    fun refreshAfterHostGame(){}

    /**
     * will be updated after a session has been joined
     */
    fun refreshAfterJoinGame(opponents : List<String>){}

    /**
     * will be updated as soon as joins lobby
     */
    fun refreshAfterPlayerJoined(sender: String){}

    /**
     * will be updated as soon as player leaves lobby
     */
    fun refreshAfterPlayerLeft(sender: String){}

    /**
     * will be updated as soon as player leaves lobby
     */
    fun refreshAfterError(msg: String){}
}
