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

/**
 * Simple in-memory repository for countries.
 */
class InMemoryCountriesRepository(
    private val ioDispatcher: IoDispatcher
) : CountriesRepository {

    /**
     * Current country to guess.
     */
    private var currentCountry: Country = AVAILABLE_COUNTRIES[0]

    /**
     * Current country flow.
     */
    private val currentCountryFlow = MutableSharedFlow<Country>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * Overriding [CountriesRepository.getAvailableCountries]
     */
    override suspend fun getAvailableCountries(): List<Country> =
        withContext(ioDispatcher.value) {
            return@withContext AVAILABLE_COUNTRIES
        }

    /**
     * Overriding [CountriesRepository.getAvailableCountries] by region.
     */
    override suspend fun getAvailableCountries(region: Region): List<Country> =
        withContext(ioDispatcher.value) {
            return@withContext AVAILABLE_COUNTRIES.filter { it.region == region }
        }

    /**
     * Overriding [CountriesRepository.setCurrentCountry]
     */
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

    /**
     * Overriding [CountriesRepository.resetCurrentCountry]
     */
    override fun resetCurrentCountry(country: Country) {
        currentCountry = country
    }

    /**
     * Constants and countries.
     */
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
            Country(
                7,
                R.string.al_name,
                Region.Europe,
                R.drawable.ic_al,
                R.string.al_hint
            ),
            Country(
                8,
                R.string.am_name,
                Region.Europe,
                R.drawable.ic_am,
                R.string.am_hint
            ),
            Country(
                9,
                R.string.ao_name,
                Region.Africa,
                R.drawable.ic_ao,
                R.string.ao_hint
            ),
            Country(
                10,
                R.string.ar_name,
                Region.SouthAmerica,
                R.drawable.ic_ar,
                R.string.ar_hint
            ),
            Country(
                11,
                R.string.as_name,
                Region.Oceania,
                R.drawable.ic_as,
                R.string.as_hint
            ),
            Country(
                12,
                R.string.at_name,
                Region.Europe,
                R.drawable.ic_at,
                R.string.at_hint
            ),
            Country(
                13,
                R.string.au_name,
                Region.Oceania,
                R.drawable.ic_au,
                R.string.au_hint
            ),
            Country(
                14,
                R.string.aw_name,
                Region.SouthAmerica,
                R.drawable.ic_aw,
                R.string.aw_hint
            ),
            Country(
                15,
                R.string.ax_name,
                Region.Europe,
                R.drawable.ic_ax,
                R.string.ax_hint
            ),
            Country(
                16,
                R.string.az_name,
                Region.Europe,
                R.drawable.ic_az,
                R.string.az_hint
            ),
            Country(
                17,
                R.string.ba_name,
                Region.Europe,
                R.drawable.ic_ba,
                R.string.ba_hint
            ),
            Country(
                18,
                R.string.bb_name,
                Region.NorthAmerica,
                R.drawable.ic_bb,
                R.string.bb_hint
            ),
            Country(
                19,
                R.string.bd_name,
                Region.Asia,
                R.drawable.ic_bd,
                R.string.bd_hint
            ),
            Country(
                20,
                R.string.be_name,
                Region.Europe,
                R.drawable.ic_be,
                R.string.be_hint
            ),
            Country(
                21,
                R.string.bf_name,
                Region.Africa,
                R.drawable.ic_bf,
                R.string.bf_hint
            ),
            Country(
                22,
                R.string.bg_name,
                Region.Europe,
                R.drawable.ic_bg,
                R.string.bg_hint
            ),
            Country(
                23,
                R.string.bh_name,
                Region.Asia,
                R.drawable.ic_bh,
                R.string.bh_hint
            ),
        )
    }

}