package com.kigya.quaso.views.quiz

import com.kigya.quaso.model.countries.Country

data class DisplayedCountry (
    val country: Country,
    val isCurrent: Boolean
)