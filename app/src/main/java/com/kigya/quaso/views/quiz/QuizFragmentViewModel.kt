package com.kigya.quaso.views.quiz

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.kigya.foundation.model.PendingResult
import com.kigya.foundation.model.Progress
import com.kigya.foundation.model.Result
import com.kigya.foundation.model.takeSuccess
import com.kigya.foundation.sideeffects.dialogs.Dialogs
import com.kigya.foundation.sideeffects.dialogs.plugin.DialogConfig
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
import com.kigya.quaso.views.choise.SelectCountryFragment
import com.kigya.quaso.views.home.HomeViewModel.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel which manages [QuizFragment] screen.
 */
class QuizFragmentViewModel(
    screen: QuizFragment.Screen,
    private val toasts: Toasts,
    private val navigator: Navigator,
    private val resources: Resources,
    private val dialogs: Dialogs,
    private val gameRepository: GameRepositoryImpl,
    private val countriesRepository: InMemoryCountriesRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    /**
     * Initial screen state of [ResultMutableStateFlow] type.
     */
    private val _currentCountry: ResultMutableStateFlow<Country> = MutableStateFlow(
        PendingResult()
    )
    /**
     * Initial screen state of [ResultMutableStateFlow] type.
     * This values will persist after the process is killed by the system and remain available via the same object.
     */
    private var _currentAttempt =
        savedStateHandle.getMutableStateFlow(CURRENT_QUESTION, 1)

    /**
     * Actual [Boolean] state which reflects whether the user has chosen a country from [SelectCountryFragment] screen.
     */
    private var isCountryChosen = true

    /**
     * Actual game [Region] that came along with [QuizFragment] screen creation as param.
     */
    val region = screen.game.region

    /**
     * Init block is run at every time the class is instantiated.
     */
    init {
        load(region)
    }

    /**
     * Main destination (contains merged values from [_currentCountry] & [_currentAttempt])
     */
    val viewState: ResultFlow<ViewState> = combine(
        _currentCountry,
        _currentAttempt,
        ::mergeSources
    )

    /**
     * Transformation pure method for combining data from several input flows:
     * - the result of fetching country (Result<Country>)
     * - [Int] state variable to indicate initial state of a user currentAttempt.
     *
     * All values above are merged into one [ViewState] instance:
     * ```
     * Flow<Result<Country>> ----------+|
     * Flow<Int> -----------------------|--> Flow<Result<ViewState>>
     * ```
     */
    private fun mergeSources(
        country: Result<Country>, currentAttempt: Int,
    ): Result<ViewState> {
        return country.map {
            ViewState(
                country = it,
                currentAttempt = currentAttempt
            )
        }
    }

    /**
     * Getting result from [SelectCountryFragment] and set to actual state.
     */
    override fun onResult(result: Any) {
        super.onResult(result)
        isCountryChosen = result as Boolean
    }

    /**
     * Method to launch [SelectCountryFragment] screen.
     */
    fun onChangePressed() {
        navigator.launch(SelectCountryFragment.Screen())
    }

    /**
     * Setting up [_currentCountry] state with [Result] of fetching country according to region.
     */
    private fun load(region: Region) {
        with(countriesRepository) {
            if (region != Region.World) into(_currentCountry) {
                getAvailableCountries(
                    region
                ).shuffled().first()
            }
            else into(_currentCountry) {
                getAvailableCountries().shuffled().first()
            }
        }
    }

    /**
     * Showing dialog with hint.
     */
    fun showHintDialog() = viewModelScope.launch { dialogs.show(createHintDialog()) }

    /**
     * Add-on over enter fragment transition.
     */
    fun getEnterTransition() = MaterialFadeThrough().apply { duration = 500 }

    /**
     * Getting actual country according to user choice.
     */
    fun getCurrentChoise(): Int {
        return gameRepository.getCurrentChoise().name
    }

    /**
     * Main controller for managing user attempt actions.
     */
    fun onTrigger(list: List<ImageView>) {
        if (isCountryChosen) {
            val currentCountry = _currentCountry.value.takeSuccess()
            with(gameRepository) {
                val currentAttempt = getCurrentAttempt()
                if (getCurrentChoise().id != (currentCountry?.id
                        ?: 0) && currentAttempt < MAX_ATTEMPTS
                ) {
                    setCurrentAttempt(currentAttempt + 1)
                    restoreOverlay(list)
                    hideOverlayItem(list)
                    notifyCheckListUpdates(list)
                } else {
                    val currentPoints = getCurrentAttempt()
                    showToastCorrectAnswer(currentCountry, currentPoints)
                    setTotalPoints(
                        resources,
                        getTotalPoints(resources) + MAX_POINTS - currentPoints
                    )
                    setLatestMode(resources, region.nameFull)
                    setLatestsPoints(resources, MAX_POINTS - currentPoints)
                    resetState()
                }
            }
        } else {
            isCountryChosen = false
            restoreOverlay(list)
        }
    }

    /**
     * Method that shows toast with correct answer.
     */
    private fun showToastCorrectAnswer(
        currentCountry: Country?,
        currentPoints: Int
    ) {
        currentCountry?.name?.let { resources.getString(it) }
            ?.let { toasts.toast("$it: +${7 - currentPoints}") }
    }

    /**
     * Method that restores check overlay items to their latest state.
     */
    private fun GameRepositoryImpl.notifyCheckListUpdates(list: List<ImageView>) {
        list.forEachIndexed { index, imageView ->
            checkVisibilityList[index] = imageView.isVisible
        }
    }

    /**
     * Method that hides one overlay item.
     */
    private fun hideOverlayItem(list: List<ImageView>) {
        list.find { it.isVisible }?.visibility = View.INVISIBLE
    }

    /**
     * Getting percentage ratio between current attempt and maximum attempts.
     */
    fun getProgressPercentage() = (gameRepository.getCurrentAttempt() * 100).toDouble() / 6


    /**
     * Method that restores sreen items to their initial state.
     */
    fun onSkipPressed() {
        resetState()
    }

    /**
     * Go back to [HomeFragment] screen and reset state.
     */
    fun onReturnPressed() {
        resetState()
        navigator.goBack()
    }

    /**
     * Add-on over exit fragment transition.
     */
    fun getExitTranstion(): Any = MaterialSharedAxis(
        MaterialSharedAxis.Z, true
    ).apply {
        duration = 300
    }

    /**
     * Add-on over reenter fragment transition.
     */
    fun getReenterTransition(): Any = MaterialSharedAxis(
        MaterialSharedAxis.Z, false
    ).apply {
        duration = 500
    }

    /**
     * Method that resets state of the screen.
     */
    private fun resetState() {
        with(gameRepository) {
            resetCurrentChoise(InMemoryCountriesRepository.EMPTY_COUNTRY)
            setCurrentAttempt(0)
            checkVisibilityList.forEachIndexed { index, _ ->
                checkVisibilityList[index] = true
            }
        }
        countriesRepository.resetCurrentCountry(InMemoryCountriesRepository.EMPTY_COUNTRY)
        _currentCountry.value = PendingResult()
        _currentAttempt.value = 1
        load(region)
    }

    /**
     * Method that restores screen overlay items to their latest state.
     */
    private fun restoreOverlay(list: List<ImageView>) {
        gameRepository.checkVisibilityList.forEachIndexed { index, check ->
            list[index].isVisible = check
        }
    }

    /**
     * @return [DialogConfig] instance with hint dialog configuration.
     */
    private fun createHintDialog(): DialogConfig {
        return DialogConfig(
            title = "Hint!",
            message = resources.getString(_currentCountry.value.takeSuccess()?.hint ?: 0),
            positiveButton = "OK"
        )
    }

    /**
     * [ViewState] declaration
     */
    data class ViewState(
        val country: Country,
        val currentAttempt: Int
    )

    /**
     * ViewModel constants.
     */
    companion object {
        private const val CURRENT_QUESTION = "current_question"
        private const val MAX_ATTEMPTS = 6
        private const val MAX_POINTS = 7
    }

}