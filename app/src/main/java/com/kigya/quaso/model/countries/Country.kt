package com.kigya.quaso.model.countries

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kigya.quaso.model.region.Region

data class Country(
    val id: Long,
    @StringRes val name: Int,
    val region: Region,
    @DrawableRes val bigFlag: Int,
    @DrawableRes val smallFlag: Int,
    @StringRes val hint: Int
)
