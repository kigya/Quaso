package com.kigya.quaso

import com.kigya.foundation.SingletonScopeDependencies
import com.kigya.foundation.model.coroutines.IoDispatcher
import com.kigya.foundation.model.coroutines.WorkerDispatcher
import com.kigya.quaso.model.countries.InMemoryCountriesRepository
import com.kigya.quaso.model.game.GameRepositoryImpl
import com.kigya.quaso.model.region.RegionsRepositoryImpl

object Initializer {

    /**
     * Singleton scope dependencies.
     */
    fun initDependencies() = SingletonScopeDependencies.init {
        val ioDispatcher = IoDispatcher() // for IO operations
        val workerDispatcher = WorkerDispatcher() // for CPU-intensive operations
        return@init listOf(
            ioDispatcher,
            workerDispatcher,
            RegionsRepositoryImpl(ioDispatcher),
            GameRepositoryImpl(ioDispatcher),
            InMemoryCountriesRepository(ioDispatcher)
        )
    }
}