package service.minimax

import service.RootService
import java.lang.Double.max
import java.lang.Double.min

/**
 * Ai algorithm
 *
 * @property rootService
 * @constructor Create Ai algorithm object
 */
class AiAlgorithm(val rootService: RootService) {

    /**
     * Returns the best action for the current player to take given the current state of the game
     * and the specified search depth. The function uses the minimax algorithm to determine the
     * best action.
     *
     * @param state The current state of the game
     * @param depth The search depth for the minimax algorithm
     * @param numPlayers the number of players
     * @return The best action for the current player to take
     */
    fun bestAction(state: State, depth: Int, numPlayers: Int): Action? {
        var bestValue = Double.NEGATIVE_INFINITY
        var bestAction: Action? = null
        for (action in state.legalActions(state)) {
            val newState = state.result(state, action)
            val value =
                minimax(
                    newState,
                    depth - 1,
                    Pair(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
                    false,
                    numPlayers
                )
            if (value > bestValue) {
                bestValue = value
                bestAction = action
            }
        }
        return bestAction
    }

    private fun minimax(
        state: State,
        depth: Int,
        intAlphaBeta: Pair<Double, Double>,
        maximizingPlayer: Boolean,
        numPlayers: Int
    ): Double {
        var alpha = intAlphaBeta.first
        var beta = intAlphaBeta.first
        if (depth == 0 || rootService.aiService.isGameOver(state)) {
            return state.heuristicValue(state)
        }

        if (maximizingPlayer) {
            var value = Double.NEGATIVE_INFINITY
            for (action in state.legalActions(state)) {
                val newState = state.result(state, action)
                val newValue = minimax(newState, depth - 1, Pair(alpha, beta), false, numPlayers)
                if (newValue > value) {
                    value = newValue
                }
                alpha = max(alpha, value)
                if (alpha >= beta) {
                    break
                }
            }
            return value
        } else {
            var value = Double.POSITIVE_INFINITY
            repeat (numPlayers) {
                for (playerAction in state.legalActions(state)) {
                    val newState = state.result(state, playerAction)
                    val newValue = minimax(newState, depth - 1, Pair(alpha, beta), true, numPlayers - 1)
                    if (newValue < value) {
                        value = newValue
                    }
                    beta = min(beta, value)
                    if (alpha >= beta) {
                        break
                    }
                }
            }
            return value
        }
    }
}
