package com.kigya.quaso

import android.app.Application

import com.kigya.foundation.BaseApplication
import com.kigya.foundation.model.coroutines.IoDispatcher
import com.kigya.foundation.model.coroutines.WorkerDispatcher
import com.kigya.quaso.model.countries.InMemoryCountriesRepository
import com.kigya.quaso.model.game.GameRepositoryImpl
import com.kigya.quaso.model.region.RegionsRepositoryImpl

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

    private val ioDispatcher = IoDispatcher() // for IO operations
    private val workerDispatcher = WorkerDispatcher() // for CPU-intensive operations

    /**
     * Singleton scope dependencies.
     */
    override val singletonScopeDependencies: List<Any> = listOf(
        ioDispatcher,
        workerDispatcher,
        RegionsRepositoryImpl(ioDispatcher),
        GameRepositoryImpl(ioDispatcher),
        InMemoryCountriesRepository(ioDispatcher)
    )

}