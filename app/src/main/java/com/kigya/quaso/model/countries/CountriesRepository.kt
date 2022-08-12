package com.kigya.quaso.model.countries

import com.kigya.foundation.model.Repository
import com.kigya.quaso.model.region.Region
import kotlinx.coroutines.flow.Flow

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
     * Get the country content by its ID
     */
    suspend fun getById(id: Long): Country

    /**
     * Get current country.
     */
    fun getCurrentCountry(): Country

    /**
     * Set the specified country as current.
     */
    fun setCurrentCountry(country: Country): Flow<Int>

    fun resetCurrentCountry(country: Country)

    /**
     * Listen for further changes of the current country.
     */
    fun listenCurrentCountry(): Flow<Country>

}