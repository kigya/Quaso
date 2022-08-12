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

class GameRepositoryImpl(
    private val ioDispatcher: IoDispatcher
) : GameRepository {

    var checkVisibilityList = mutableListOf(
        true, true, true, true, true, true
    )

    private var latestGame: Game = game

    private var currentChoise: Country = InMemoryCountriesRepository.EMPTY_COUNTRY

    private var currentAttempt = 0

    private var totalPoints = 0

    private var latestPoints = 0

    private var latestMode = "NaN"


    private val totalPointsFlow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val latestGameFlow = MutableSharedFlow<Game>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val currentQuestionNumberFlow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val currentChoiseFlow = MutableSharedFlow<Country>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override suspend fun getLatestGame(): Game =
        withContext(ioDispatcher.value) {
            return@withContext latestGame
        }

    override fun getCurrentAttempt(): Int = currentAttempt

    override fun getCurrentChoise(): Country = currentChoise


    override fun getTotalPoints(resources: Resources): Int =
        resources.getSharedPreferences(
            POINTS_PREFERENCES,
            Context.MODE_PRIVATE
        ).getInt(TOTAL_POINTS, 0)

    override fun getLatestMode(resources: Resources): String {
        val i = resources.getSharedPreferences(
            POINTS_PREFERENCES,
            Context.MODE_PRIVATE
        ).getString(LATEST_MODE, resources.getString(R.string.nan)) ?: "NaN"
        return i
    }

    override fun getLatestPoints(resources: Resources): Int =
        resources.getSharedPreferences(
            POINTS_PREFERENCES,
            Context.MODE_PRIVATE
        ).getInt(LATEST_POINTS, 0)

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

    override fun resetCurrentChoise(country: Country) {
        currentChoise = country
    }


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

    override fun setCurrentAttempt(int: Int) {
        currentAttempt = int
    }

    override fun setTotalPoints(resources: Resources, int: Int) = resources.getSharedPreferences(
        POINTS_PREFERENCES,
        Context.MODE_PRIVATE
    ).edit().putInt(TOTAL_POINTS, int).apply()

    override fun setLatestsPoints(resources: Resources, int: Int) = resources.getSharedPreferences(
        POINTS_PREFERENCES,
        Context.MODE_PRIVATE
    ).edit().putInt(LATEST_POINTS, int).apply()


    override fun setLatestMode(resources: Resources, string: String) =
        resources.getSharedPreferences(
            POINTS_PREFERENCES,
            Context.MODE_PRIVATE
        ).edit().putString(LATEST_MODE, string).apply()


    override fun listenCurrentChoise(): Flow<Country> {
        TODO("Not yet implemented")
    }


    override fun listenLatestGame(): Flow<Game> = latestGameFlow

    override fun listenTotalPoints(): Flow<Int> = totalPointsFlow

    override fun listenCurrentQuestionNumber(): Flow<Int> = currentQuestionNumberFlow

    companion object {
        val game = Game(Region.World, 0)
        const val POINTS_PREFERENCES = "points_preferences"
        const val TOTAL_POINTS = "total_points"
        const val LATEST_MODE = "latest_mode"
        const val LATEST_POINTS = "latest_points"
    }
}
