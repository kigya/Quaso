package com.kigya.quaso.model.countries

import com.kigya.foundation.model.coroutines.IoDispatcher
import com.kigya.quaso.R
import com.kigya.quaso.model.region.Region
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class InMemoryCountriesRepository(
    private val ioDispatcher: IoDispatcher
) : CountriesRepository {

    private var currentCountry: Country = AVAILABLE_COUNTRIES[0]

    private val currentCountryFlow = MutableSharedFlow<Country>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override suspend fun getAvailableCountries(): List<Country> =
        withContext(ioDispatcher.value) {
            return@withContext AVAILABLE_COUNTRIES
        }

    override suspend fun getAvailableCountries(region: Region): List<Country> =
        withContext(ioDispatcher.value) {
            return@withContext AVAILABLE_COUNTRIES.filter { it.region == region }
        }

    override suspend fun getById(id: Long): Country =
        withContext(ioDispatcher.value) {
            return@withContext AVAILABLE_COUNTRIES.first { it.id == id }
        }

    override suspend fun getCurrentCountry(): Country =
        withContext(ioDispatcher.value) {
            return@withContext currentCountry
        }

    override fun setCurrentCountry(country: Country): Flow<Int> = flow {
        if (currentCountry != country) {
            var progress = 0
            while (progress < 100) {
                progress += 2
                delay(30)
                emit(progress)
            }
            currentCountry = country
            currentCountryFlow.emit(country)
        } else {
            emit(100)
        }
    }.flowOn(ioDispatcher.value)

    override fun listenCurrentCountry(): Flow<Country> = currentCountryFlow

    companion object {
        private val AVAILABLE_COUNTRIES = listOf(
            Country(
                1,
                R.string.ac_name,
                Region.Africa,
                R.drawable.ic_ac,
                R.drawable.ic_ac_small,
                R.string.ac_hint
            )
        )
    }

}