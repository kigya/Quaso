package com.kigya.quaso.views.quiz

import androidx.lifecycle.SavedStateHandle
import com.kigya.foundation.model.*
import com.kigya.foundation.views.*
import com.kigya.quaso.model.countries.Country
import com.kigya.quaso.model.game.GameRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import com.kigya.foundation.model.Result
import com.kigya.foundation.sideeffects.navigator.Navigator
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.quaso.R
import com.kigya.quaso.model.countries.InMemoryCountriesRepository
import com.kigya.quaso.model.region.Region

class QuizFragmentViewModel(
    screen: QuizFragment.Screen,
    private val navigator: Navigator,
    private val gameRepository: GameRepositoryImpl,
    private val countriesRepository: InMemoryCountriesRepository,
    private val resources: Resources,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _availableCountries: ResultMutableStateFlow<List<Country>> = MutableStateFlow(
        PendingResult()
    )
    private var _currentCountryId =
        savedStateHandle.getMutableStateFlow("current_country_id", 1L)


    val viewState: ResultFlow<ViewState> = combine(
        _availableCountries,
        _currentCountryId,
        ::mergeSources
    )

    init {
        load(screen.game.region)
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain(screen: QuizFragment.Screen) = load(screen.game.region)


    private fun mergeSources(
        countries: Result<List<Country>>, currentCountryId: Long
    ): Result<ViewState> {
        // map Result<List<NamedColor>> to Result<ViewState>
        return countries.map { countriesList ->
            ViewState(
                // map List<NamedColor> to List<NamedColorListItem>
                countriesList = countriesList.map {
                    DisplayedCountry(
                        it,
                        isCurrent = currentCountryId == it.id
                    )
                },
                showNextButton = true
            )
        }
    }

    private fun load(region: Region) =
        into(_availableCountries) { countriesRepository.getAvailableCountries(region) }

    data class ViewState(
        val countriesList: List<DisplayedCountry>,
        val showNextButton: Boolean,
    )

}