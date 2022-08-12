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

    override fun getCurrentCountry(): Country = currentCountry

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

    override fun resetCurrentCountry(country: Country) {
        currentCountry = country
    }

    override fun listenCurrentCountry(): Flow<Country> = currentCountryFlow

    companion object {
        val EMPTY_COUNTRY = Country(0, R.string.nan, Region.World, R.drawable.ic_aq, R.string.empty)
        private val AVAILABLE_COUNTRIES = listOf(
            Country(
                1,
                R.string.ac_name,
                Region.Africa,
                R.drawable.ic_ac,
                R.string.ac_hint
            ),
            Country(
                2,
                R.string.ad_name,
                Region.Europe,
                R.drawable.ic_ad,
                R.string.ad_hint
            ),
            Country(
                3,
                R.string.ae_name,
                Region.Asia,
                R.drawable.ic_ae,
                R.string.ae_hint
            ),
            Country(
                4,
                R.string.af_name,
                Region.Asia,
                R.drawable.ic_af,
                R.string.af_hint
            ),
            Country(
                5,
                R.string.ag_name,
                Region.NorthAmerica,
                R.drawable.ic_ag,
                R.string.ag_hint
            ),
            Country(
                6,
                R.string.ai_name,
                Region.NorthAmerica,
                R.drawable.ic_ai,
                R.string.ai_hint
            ),
        )
    }

}