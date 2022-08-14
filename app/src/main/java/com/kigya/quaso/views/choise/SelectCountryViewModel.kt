package com.kigya.quaso.views.choise

import android.widget.SearchView
import androidx.lifecycle.viewModelScope
import com.google.android.material.transition.MaterialSharedAxis
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

/**
 * ViewModel which manages [SelectCountryFragment] screen.
 */
class SelectCountryViewModel(
    val resources: Resources,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val gameRepository: GameRepositoryImpl,
    private val countriesRepository: InMemoryCountriesRepository,
) : BaseViewModel(), CountriesAdapter.Listener, SearchView.OnQueryTextListener {

    /**
     * Initial screen loading state of [MutableStateFlow] type.
     */
    private val _instantSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)
    /**
     * Initial screen loading state of [MutableStateFlow] type.
     */
    private val _sampledSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)
    /**
     * Initial screen state of [ResultMutableStateFlow] type.
     */
    private val _availableCountries: ResultMutableStateFlow<List<Country>> = MutableStateFlow(
        PendingResult()
    )

    /**
     * [CountriesAdapter] instance.
     */
    val adapter: CountriesAdapter = CountriesAdapter(this, resources)

    /**
     * Main destination (contains merged values from [_availableCountries], [_instantSaveInProgress] & [_sampledSaveInProgress])
     */
    val viewState: ResultFlow<ViewState> = combine(
        _availableCountries,
        _instantSaveInProgress,
        _sampledSaveInProgress,
        ::mergeSources
    )

    /**
     * Init block is run at every time the class is instantiated.
     */
    init {
        load()
    }

    /**
     * Overriding [SearchView.OnQueryTextListener.onQueryTextSubmit] method.
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        adapter.filter.filter(query)
        return false
    }

    /**
     * Overriding [SearchView.OnQueryTextListener.onQueryTextChange] method.
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.filter.filter(newText)
        return false
    }

    /**
     * Overriding [CountriesAdapter.Listener.onCountryChosen] method.
     */
    @OptIn(FlowPreview::class)
    override fun onCountryChosen(country: Country) {
        onItemClicked(country)
    }

    /**
     * Actions to perform when [CountriesAdapter.Listener.onCountryChosen] is called.
     */
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

            navigator.goBack(true)
        } catch (e: Exception) {
            if (e !is CancellationException)
                toasts.toast(resources.getString(R.string.error_happened))
        } finally {
            _instantSaveInProgress.value = EmptyProgress
            _sampledSaveInProgress.value = EmptyProgress
        }
    }

    /**
     * Transformation pure method for combining data from several input flows:
     * - the result of fetching countries list (Result<List<Country>>)
     * - [Progress] instance which indicates whether saving operation is in
     *   progress or not
     *
     * All values above are merged into one [ViewState] instance:
     * ```
     * Flow<Result<List<Country>>> ----------+|
     * Flow<Progress> -----------------------|--> Flow<Result<ViewState>>
     * ```
     */
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

    /**
     * Loads countries list from [InMemoryCountriesRepository] and sets it to [_availableCountries] flow.
     */
    private fun load() {
        into(_availableCountries) {
            delay(1000) // emitation of the first result is delayed to show the progress bar
            countriesRepository.getAvailableCountries().sortedBy { resources.getString(it.name) }
        }
    }

    /**
     * Do the same as [load] when error occurs.
     */
    fun tryAgain() = load()

    /**
     * Return screen back without actions.
     */
    fun onReturnPressed() {
        navigator.goBack(false)
    }

    /**
     * Getting enter fragment transition.
     */
    fun getEnterTransition(): Any = MaterialSharedAxis(
        MaterialSharedAxis.Z, true
    ).apply {
        duration = 1000
    }

    /**
     * Getting return fragment transition.
     */
    fun getReturnTransition(): Any = MaterialSharedAxis(
        MaterialSharedAxis.Z, false
    ).apply {
        duration = 500
    }

    /**
     * ViewState class which is used to combine data from several input flows
     */
    data class ViewState(
        val countries: List<Country>,
        val showLoadingProgressBar: Boolean,
        val saveProgressPercentage: Int,
        val saveProgressPercentageMessage: String,
    )
}