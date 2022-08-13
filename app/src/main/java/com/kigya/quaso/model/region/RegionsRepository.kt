package com.kigya.quaso.model.region

import com.kigya.foundation.model.Repository

/**
 * Repository interface for the regions.
 *
 * Provides access to the available regions.
 */
interface RegionsRepository: Repository {

    /**
     * Get the list of all available regions that may be chosen by the user.
     */
    suspend fun getAvailableRegions(): List<Region>

}