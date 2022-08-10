package com.kigya.quaso.views.quiz

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kigya.foundation.model.PendingResult
import com.kigya.foundation.model.Result
import com.kigya.foundation.model.SuccessResult
import com.kigya.foundation.sideeffects.navigator.Navigator
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.foundation.sideeffects.toasts.Toasts
import com.kigya.foundation.views.BaseViewModel
import com.kigya.foundation.views.ResultFlow
import com.kigya.foundation.views.ResultMutableStateFlow
import com.kigya.quaso.model.countries.Country
import com.kigya.quaso.model.countries.InMemoryCountriesRepository
import com.kigya.quaso.model.game.GameRepositoryImpl
import com.kigya.quaso.model.region.Region
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class QuizFragmentViewModel(
    screen: QuizFragment.Screen,
    private val toasts: Toasts,
    private val navigator: Navigator,
    private val resources: Resources,
    private val gameRepository: GameRepositoryImpl,
    private val countriesRepository: InMemoryCountriesRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _availableCountries: ResultMutableStateFlow<List<Country>> = MutableStateFlow(
        PendingResult()
    )
    private var _currentCountryId =
        savedStateHandle.getMutableStateFlow(CURRENT_COUNTRY_ID, 1L)

    private var _currentAttempt =
        savedStateHandle.getMutableStateFlow(CURRENT_QUESTION, 1)

    private var isNextActive = true

    val viewState: ResultFlow<ViewState> = combine(
        _availableCountries,
        _currentCountryId,
        _currentAttempt,
        ::mergeSources
    )

    val region = screen.game.region

    init {
        load(region)
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    private fun mergeSources(
        countries: Result<List<Country>>, currentCountryId: Long, currentAttempt: Int,
    ): Result<ViewState> {
        return countries.map { countriesList ->
            ViewState(
                countriesList = countriesList,
                currentAttempt = currentAttempt,
                showNextButton = isNextActive,
            )
        }
    }

    private fun load(region: Region) =
        if (region != Region.World) into(_availableCountries) {
            countriesRepository.getAvailableCountries(
                region
            ).shuffled()
        }
        else into(_availableCountries) {
            countriesRepository.getAvailableCountries().shuffled()
        }

    fun onAttemptUsed(list: List<ImageView>) {
        _currentAttempt.value += 1
        viewModelScope.launch {
            viewState.collect {
                if (it is SuccessResult) {
                    hideOverlayItem(list)
                }
            }
        }
    }

    fun hideOverlayItem(list: List<ImageView>) {
        list.find { it.isVisible }?.visibility = View.INVISIBLE
    }


    data class ViewState(
        val countriesList: List<Country>,
        val currentAttempt: Int,
        val showNextButton: Boolean
    )

    companion object {
        private const val CURRENT_COUNTRY_ID = "current_country_id"
        private const val CURRENT_QUESTION = "current_question"
    }

}