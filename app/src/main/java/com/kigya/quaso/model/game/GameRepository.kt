package com.kigya.quaso.model.game

import com.kigya.foundation.model.Repository
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.quaso.model.countries.Country
import kotlinx.coroutines.flow.Flow

/**
 * Game interface repository.
 */
interface GameRepository : Repository {

    /**
     * Get latest game.
     */
    suspend fun getLatestGame(): Game

    /**
     * Get current attempt.
     */
    fun getCurrentAttempt(): Int

    /**
     * Get current country choise.
     */
    fun getCurrentChoise(): Country

    /**
     * Get total points.
     */
    fun getTotalPoints(resources: Resources): Int

    /**
     * Get latest mode.
     */
    fun getLatestMode(resources: Resources): String

    /**
     * Get latest points.
     */
    fun getLatestPoints(resources: Resources): Int

    /**
     * Set up current country choise.
     */
    fun setCurrentChoise(country: Country): Flow<Int>

    /**
     * Reset current country choise.
     */
    fun resetCurrentChoise(country: Country)

    /**
     * Set latest game.
     */
    fun setLatestGame(game: Game): Flow<Int>

    /**
     * Set current user attempt.
     */
    fun setCurrentAttempt(int: Int)

    /**
     * Set total points.
     */
    fun setTotalPoints(resources: Resources, int: Int)

    /**
     * Set latest points.
     */
    fun setLatestsPoints(resources: Resources, int: Int)

    /**
     * Set latest mode.
     */
    fun setLatestMode(resources: Resources, string: String)

}