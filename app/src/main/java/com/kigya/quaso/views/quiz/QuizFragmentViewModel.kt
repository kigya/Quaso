package com.kigya.quaso.views.quiz

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.util.rangeTo
import androidx.core.view.isVisible
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.kigya.foundation.model.PendingResult
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

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

    private val _currentCountry: ResultMutableStateFlow<Country> = MutableStateFlow(
        PendingResult()
    )

    private var _currentAttempt =
        savedStateHandle.getMutableStateFlow(CURRENT_QUESTION, 1)

    var choiseCountry: Country = gameRepository.getCurrentChoise()
    var currentAttempt = gameRepository.getCurrentAttempt()
    val region = screen.game.region
    var isCountryChosen = true

    init {
        load(region)
    }

    val viewState: ResultFlow<ViewState> = combine(
        _currentCountry,
        _currentAttempt,
        ::mergeSources
    )

    fun onChangePressed() {
        navigator.launch(SelectCountryFragment.Screen())
    }

    private fun mergeSources(
        country: Result<Country>, currentAttempt: Int,
    ): Result<ViewState> {
        return country.map { country ->
            ViewState(
                country = country,
                currentAttempt = gameRepository.getCurrentAttempt()
            )
        }
    }

    private fun load(region: Region) {
        if (region != Region.World) into(_currentCountry) {
            countriesRepository.getAvailableCountries(
                region
            ).shuffled().first()
        }
        else into(_currentCountry) {
            countriesRepository.getAvailableCountries().shuffled().first()
        }
    }

    fun showHintDialog() = viewModelScope.launch { dialogs.show(createHintDialog()) }

    fun getCurrentChoise(): Int {
        return gameRepository.getCurrentChoise().name
    }

    private fun restoreOverlay(list: List<ImageView>) {
        gameRepository.checkVisibilityList.forEachIndexed { index, check ->
            list[index].isVisible = check
        }
    }

    fun onTrigger(list: List<ImageView>) {
        if (isCountryChosen) {
            if ((gameRepository.getCurrentChoise().id != (_currentCountry.value.takeSuccess()?.id
                    ?: 0) && (gameRepository.getCurrentAttempt() < 6))
            ) {
                gameRepository.setCurrentAttempt(gameRepository.getCurrentAttempt() + 1)
                restoreOverlay(list)
                list.find { it.isVisible }?.visibility = View.INVISIBLE
                list.forEachIndexed { index, imageView ->
                    gameRepository.checkVisibilityList[index] = imageView.isVisible
                }
            } else {
                val currentPoints = gameRepository.getCurrentAttempt()
                _currentCountry.value.takeSuccess()?.name?.let { resources.getString(it) }
                    ?.let { toasts.toast("$it: +${7 - currentPoints}") }
                gameRepository.setTotalPoints(
                    resources,
                    gameRepository.getTotalPoints(resources) + 7 - currentPoints
                )
                gameRepository.setLatestMode(resources, region.nameFull)
                gameRepository.setLatestsPoints(resources, 7 - currentPoints)
                resetState()
            }
        } else {
            isCountryChosen = false
            restoreOverlay(list)
        }

    }

    private fun resetState() {
        countriesRepository.resetCurrentCountry(InMemoryCountriesRepository.EMPTY_COUNTRY)
        gameRepository.resetCurrentChoise(InMemoryCountriesRepository.EMPTY_COUNTRY)
        gameRepository.setCurrentAttempt(0)
        gameRepository.checkVisibilityList.forEachIndexed { index, _ ->
            gameRepository.checkVisibilityList[index] = true
        }
        _currentCountry.value = PendingResult()
        _currentAttempt.value = 1
        load(region)
    }

    fun getProgressPercentage() = (gameRepository.getCurrentAttempt() * 100).toDouble() / 6

    data class ViewState(
        val country: Country,
        val currentAttempt: Int
    )

    private fun createHintDialog(): DialogConfig {
        return DialogConfig(
            title = "Hint!",
            message = resources.getString(_currentCountry.value.takeSuccess()?.hint ?: 0),
            positiveButton = "OK"
        )
    }

    fun onSkipPressed() {
        resetState()
    }

    fun onReturnPressed() {
        resetState()
        navigator.goBack()
    }

    override fun onResult(result: Any) {
        super.onResult(result)
        isCountryChosen = result as Boolean
    }

    fun getExitTranstion(): Any = MaterialSharedAxis(
        MaterialSharedAxis.Z, true
    ).apply {
        duration = 300
    }

    fun getReenterTransition(): Any = MaterialSharedAxis(
        MaterialSharedAxis.Z, false
    ).apply {
        duration = 500
    }

    fun getEnterTransition() = MaterialFadeThrough().apply { duration = 500 }

    companion object {
        private const val CURRENT_QUESTION = "current_question"
    }

}