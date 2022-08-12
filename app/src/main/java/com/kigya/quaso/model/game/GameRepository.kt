package com.kigya.quaso.model.game

import com.kigya.foundation.model.Repository
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.quaso.model.countries.Country
import kotlinx.coroutines.flow.Flow

interface GameRepository : Repository {

    suspend fun getLatestGame(): Game

    fun getCurrentAttempt(): Int

    fun getCurrentChoise(): Country

    fun getTotalPoints(resources: Resources): Int

    fun getLatestMode(resources: Resources): String

    fun getLatestPoints(resources: Resources): Int

    fun setCurrentChoise(country: Country): Flow<Int>

    fun resetCurrentChoise(country: Country)

    fun setLatestGame(game: Game): Flow<Int>

    fun setCurrentAttempt(int: Int)

    fun setTotalPoints(resources: Resources, int: Int)

    fun setLatestsPoints(resources: Resources, int: Int)

    fun setLatestMode(resources: Resources, string: String)

    fun listenCurrentChoise(): Flow<Country>

    fun listenLatestGame(): Flow<Game>

    fun listenTotalPoints(): Flow<Int>

    fun listenCurrentQuestionNumber(): Flow<Int>

}