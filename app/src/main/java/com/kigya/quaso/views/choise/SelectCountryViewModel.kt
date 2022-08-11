package com.kigya.quaso.views.choise

import androidx.appcompat.widget.SearchView
import androidx .lifecycle.viewModelScope
import com.kigya.foundation.model.*
import com.kigya.foundation.sideeffects.navigator.Navigator
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.foundation.sideeffects.toasts.Toasts
import com.kigya.foundation.utils.finiteShareIn
import com.kigya.foundation.views.BaseViewModel
import com.kigya.foundation.views.ResultFlow
import com.kigya.foundation.views.ResultMutableStateFlow
import com.kigya.quaso.R
import com.kigya.quaso.model.countries.Country
import com.kigya.quaso.model.countries.InMemoryCountriesRepository
import com.kigya.quaso.model.game.GameRepositoryImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.sample


class SelectCountryViewModel(
    val resources: Resources,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val gameRepository: GameRepositoryImpl,
    private val countriesRepository: InMemoryCountriesRepository,
) : BaseViewModel(), CountriesAdapter.Listener {

    private val _instantSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)
    private val _sampledSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)
    private val _availableCountries: ResultMutableStateFlow<List<Country>> = MutableStateFlow(
        PendingResult()
    )

    val viewState: ResultFlow<SelectCountryViewModel.ViewState> = combine(
        _availableCountries,
        _instantSaveInProgress,
        _sampledSaveInProgress,
        ::mergeSources
    )

    init {
        load()
    }

    override fun onCountryChosen(country: Country) {
        if (_instantSaveInProgress.value.isInProgress()) return
        onItemClicked(country)
    }

    @FlowPreview
    fun onItemClicked(country: Country) = viewModelScope.launch {
        try {
            _instantSaveInProgress.value = PercentageProgress.START
            _sampledSaveInProgress.value = PercentageProgress.START

            val flow = gameRepository.setCurrentChoise(country).finiteShareIn(this)

            val instantJob = async {
                flow.collect { percentage ->
                    _instantSaveInProgress.value = PercentageProgress(percentage)
                }
            }

            val sampledJob = async {
                flow.sample(200) // emit the most actual progress every 200ms.
                    .collect { percentage ->
                        _sampledSaveInProgress.value = PercentageProgress(percentage)
                    }
            }

            instantJob.await()
            sampledJob.await()

            navigator.goBack()
        } catch (e: Exception) {
            if (e !is CancellationException)
                toasts.toast(resources.getString(R.string.error_happened))
        } finally {
            _instantSaveInProgress.value = EmptyProgress
            _sampledSaveInProgress.value = EmptyProgress
        }
    }

    private fun mergeSources(
        countries: Result<List<Country>>,
        instantSaveInProgress: Progress,
        sampledSaveInProgress: Progress
    ): Result<ViewState> {
        return countries.map { countryList ->
            ViewState(
                countries = countryList,
                showLoadingProgressBar = instantSaveInProgress.isInProgress(),
                saveProgressPercentage = instantSaveInProgress.getPercentage(),
                saveProgressPercentageMessage = resources.getString(
                    R.string.percentage_value,
                    sampledSaveInProgress.getPercentage()
                ),
            )
        }
    }

    private fun load() {
        into(_availableCountries) {
            delay(1000) // emitation of the first result is delayed to show the progress bar
            countriesRepository.getAvailableCountries().sortedBy { resources.getString(it.name) }
        }
    }

    fun tryAgain() = load()

    data class ViewState(
        val countries: List<Country>,
        val showLoadingProgressBar: Boolean,
        val saveProgressPercentage: Int,
        val saveProgressPercentageMessage: String,
    )

}