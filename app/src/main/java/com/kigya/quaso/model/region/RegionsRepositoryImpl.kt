package com.kigya.quaso.model.region

import com.kigya.foundation.model.coroutines.IoDispatcher
import kotlinx.coroutines.withContext

/**
 * Simple in-memory implementation of [RegionsRepository].
 */
class RegionsRepositoryImpl(
    private val ioDispatcher: IoDispatcher
) : RegionsRepository {

    /**
     * Implementation of [RegionsRepository.getAvailableRegions] method.
     */
    override suspend fun getAvailableRegions(): List<Region> =
        withContext(ioDispatcher.value) { return@withContext regions }

    companion object {
        val regions = listOf(
            Region.World,
            Region.Europe,
            Region.Asia,
            Region.Africa,
            Region.NorthAmerica,
            Region.SouthAmerica,
            Region.Oceania
        )
    }
}
