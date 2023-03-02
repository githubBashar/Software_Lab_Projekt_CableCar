package service
import view.Refreshable

/**
 * The shell for a refreshing service
 */
abstract class AbstractRefreshingService {

    private val refreshables = mutableListOf<Refreshable>()

    /**
     * Adds [Refreshable]
     *
     * @param newRefreshable The [refreshable] to add
     */
    fun addRefreshable(newRefreshable: Refreshable){
        refreshables += newRefreshable
    }

    /**
     * Ignites the [Refreshable]
     *
     * @param method The function to be executed
     */
    fun onAllRefreshables(method: Refreshable.() -> Unit) =
        refreshables.forEach { it.method() }
}
