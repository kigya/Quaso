package com.kigya.quaso.model.game

import com.kigya.quaso.model.region.Region

data class Game(
    val region: Region,
    val points: Int
)
