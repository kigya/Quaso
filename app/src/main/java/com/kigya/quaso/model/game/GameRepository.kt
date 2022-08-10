package com.kigya.quaso.model.game

import com.kigya.foundation.model.Repository
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.quaso.model.countries.Country
import kotlinx.coroutines.flow.Flow

interface GameRepository: Repository {

    /**
     * Get latest game.
     */
    suspend fun getLatestGame(): Game

    /**
     * Get current question number.
     */
    suspend fun getCurrentQuestionNumber(): Int

    fun getTotalPoints(resources: Resources): Int

    /**
     * Set the specified game as current.
     */
    fun setLatestGame(game: Game): Flow<Int>

    /**
     * Set the specified question number as current.
     */
    fun setCurrentAttempt(int: Int): Flow<Int>

    fun setTotalPoints(int: Int): Flow<Int>

    /**
     * Listen for further changes of the game.
     */
    fun listenLatestGame(): Flow<Game>

    fun listenTotalPoints(): Flow<Int>


    /**
     * Listen for further changes of the current question number.
     */
    fun listenCurrentQuestionNumber(): Flow<Int>

}