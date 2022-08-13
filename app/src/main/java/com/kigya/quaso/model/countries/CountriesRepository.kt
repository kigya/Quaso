package com.kigya.quaso.model.countries

import com.kigya.foundation.model.Repository
import com.kigya.quaso.model.region.Region
import kotlinx.coroutines.flow.Flow

/**
 * Interface repository for countries.
 */
interface CountriesRepository : Repository {

    /**
     * Get the list of all available countries that may be chosen by the user.
     */
    suspend fun getAvailableCountries(): List<Country>

    /**
     * Get the list of all available countries depends on region.
     */
    suspend fun getAvailableCountries(region: Region): List<Country>

    /**
     * Set the specified country as current.
     */
    fun setCurrentCountry(country: Country): Flow<Int>

    /**
     * Reset the current country to default.
     */
    fun resetCurrentCountry(country: Country)

}