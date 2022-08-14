package com.kigya.quaso.model.game

import android.content.Context
import com.kigya.foundation.model.coroutines.IoDispatcher
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.quaso.R
import com.kigya.quaso.model.countries.Country
import com.kigya.quaso.model.countries.InMemoryCountriesRepository
import com.kigya.quaso.model.region.Region
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Simple in-memory implementation of [GameRepository]
 */
class GameRepositoryImpl(
    private val ioDispatcher: IoDispatcher
) : GameRepository {

    /**
     * Check list to store whether the element is hidden.
     */
    var checkVisibilityList = mutableListOf(
        true, true, true, true, true, true
    )

    /**
     * Latest game.
     */
    private var latestGame: Game = game

    /**
     * Current user choice.
     */
    private var currentChoise: Country = InMemoryCountriesRepository.EMPTY_COUNTRY

    /**
     * Current user attempt.
     */
    private var currentAttempt = 0

    /**
     * Latest game flow.
     */
    private val latestGameFlow = MutableSharedFlow<Game>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * Current choise flow.
     */
    private val currentChoiseFlow = MutableSharedFlow<Country>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * Overriding [GameRepository.getLatestGame]
     */
    override suspend fun getLatestGame(): Game =
        withContext(ioDispatcher.value) {
            return@withContext latestGame
        }

    /**
     * Overriding [GameRepository.getCurrentAttempt]
     */
    override fun getCurrentAttempt(): Int = currentAttempt

    /**
     * Overriding [GameRepository.getCurrentChoise]
     */
    override fun getCurrentChoise(): Country = currentChoise


    /**
     * Overriding [GameRepository.getTotalPoints]
     */
    override fun getTotalPoints(resources: Resources): Int =
        resources.getSharedPreferences(
            POINTS_PREFERENCES,
            Context.MODE_PRIVATE
        ).getInt(TOTAL_POINTS, 0)

    /**
     * Overriding [GameRepository.getLatestMode]
     */
    override fun getLatestMode(resources: Resources): String {
        return resources.getSharedPreferences(
            POINTS_PREFERENCES,
            Context.MODE_PRIVATE
        ).getString(LATEST_MODE, resources.getString(R.string.nan)) ?: "NaN"
    }

    /**
     * Overriding [GameRepository.getLatestPoints]
     */
    override fun getLatestPoints(resources: Resources): Int =
        resources.getSharedPreferences(
            POINTS_PREFERENCES,
            Context.MODE_PRIVATE
        ).getInt(LATEST_POINTS, 0)

    /**
     * Overriding [GameRepository.setCurrentChoise]
     */
    override fun setCurrentChoise(country: Country): Flow<Int> = flow {
        if (this@GameRepositoryImpl.currentChoise != country) {
            var progress = 0
            while (progress < 100) {
                progress += 2
                delay(10)
                emit(progress)
            }
            currentChoise = country
            currentChoiseFlow.emit(country)
        } else {
            emit(100)
        }
    }.flowOn(ioDispatcher.value)

    /**
     * Overriding [GameRepository.resetCurrentChoise]
     */
    override fun resetCurrentChoise(country: Country) {
        currentChoise = country
    }

    /**
     * Overriding [GameRepository.setLatestGame]
     */
    override fun setLatestGame(game: Game): Flow<Int> = flow {
        if (this@GameRepositoryImpl.latestGame != game) {
            var progress = 0
            while (progress < 100) {
                progress += 2
                delay(10)
                emit(progress)
            }
            latestGame = game
            latestGameFlow.emit(game)
        } else {
            emit(100)
        }
    }.flowOn(ioDispatcher.value)

    /**
     * Overriding [GameRepository.setCurrentAttempt]
     */
    override fun setCurrentAttempt(int: Int) {
        currentAttempt = int
    }

    /**
     * Overriding [GameRepository.setTotalPoints]
     */
    override fun setTotalPoints(resources: Resources, int: Int) = resources.getSharedPreferences(
        POINTS_PREFERENCES,
        Context.MODE_PRIVATE
    ).edit().putInt(TOTAL_POINTS, int).apply()

    /**
     * Overriding [GameRepository.setLatestsPoints]
     */
    override fun setLatestsPoints(resources: Resources, int: Int) = resources.getSharedPreferences(
        POINTS_PREFERENCES,
        Context.MODE_PRIVATE
    ).edit().putInt(LATEST_POINTS, int).apply()


    /**
     * Overriding [GameRepository.setLatestMode]
     */
    override fun setLatestMode(resources: Resources, string: String) =
        resources.getSharedPreferences(
            POINTS_PREFERENCES,
            Context.MODE_PRIVATE
        ).edit().putString(LATEST_MODE, string).apply()

    /**
     * Constants.
     */
    companion object {
        val game = Game(Region.World, 0)
        const val POINTS_PREFERENCES = "points_preferences"
        const val TOTAL_POINTS = "total_points"
        const val LATEST_MODE = "latest_mode"
        const val LATEST_POINTS = "latest_points"
    }
}
