package com.kigya.quaso.model.game

import com.kigya.quaso.model.region.Region

/**
 * Represents a game.
 */
data class Game(
    val region: Region,
    val points: Int
)
