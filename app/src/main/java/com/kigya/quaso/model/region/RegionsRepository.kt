package com.kigya.quaso.model.region

import com.kigya.foundation.model.Repository
import com.kigya.quaso.model.countries.Country
import kotlinx.coroutines.flow.Flow

interface RegionsRepository: Repository {

    /**
     * Get the list of all available countries that may be chosen by the user.
     */
    suspend fun getAvailableRegions(): List<Region>

}