package com.kigya.quaso.model.game

import android.content.Context
import com.kigya.foundation.model.coroutines.IoDispatcher
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.quaso.databinding.FragmentQuizBinding
import com.kigya.quaso.model.region.Region
import com.kigya.quaso.views.home.HomeViewModel
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

    private var latestGame: Game = GameRepositoryImpl.game

    private var currentQuestionNumber = 1

    private var totalPoints = 0

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

    override suspend fun getLatestGame(): Game =
        withContext(ioDispatcher.value) {
            return@withContext latestGame
        }

    override suspend fun getCurrentQuestionNumber(): Int =
        withContext(ioDispatcher.value) {
            return@withContext currentQuestionNumber
        }

    override fun getTotalPoints(resources: Resources): Int =
        resources.getSharedPreferences(
            POINTS_PREFERENCES,
            Context.MODE_PRIVATE
        ).getInt(TOTAL_POINTS, 0)


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

    override fun setCurrentAttempt(number: Int): Flow<Int> = flow {
        if (this@GameRepositoryImpl.currentQuestionNumber != number) {
            var progress = 0
            while (progress < 100) {
                progress += 2
                delay(10)
                emit(progress)
            }
            currentQuestionNumber = number
            currentQuestionNumberFlow.emit(number)
        } else {
            emit(100)
        }
    }.flowOn(ioDispatcher.value)

    override fun setTotalPoints(int: Int): Flow<Int> = flow {
        if (this@GameRepositoryImpl.totalPoints != int) {
            var progress = 0
            while (progress < 100) {
                progress += 2
                delay(10)
                emit(progress)
            }
            totalPoints = int
            totalPointsFlow.emit(int)
        } else {
            emit(100)
        }
    }.flowOn(ioDispatcher.value)


    override fun listenLatestGame(): Flow<Game> = latestGameFlow

    override fun listenTotalPoints(): Flow<Int> = totalPointsFlow

    override fun listenCurrentQuestionNumber(): Flow<Int> = currentQuestionNumberFlow

    companion object {
        val game = Game(Region.World, 0)
        private const val POINTS_PREFERENCES = "points_preferences"
        private const val TOTAL_POINTS = "total_points"
    }
}
