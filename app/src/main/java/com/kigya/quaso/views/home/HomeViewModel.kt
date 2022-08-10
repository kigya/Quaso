package com.kigya.quaso.views.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kigya.foundation.model.*
import com.kigya.foundation.sideeffects.navigator.Navigator
import com.kigya.foundation.sideeffects.resources.Resources
import com.kigya.foundation.sideeffects.toasts.Toasts
import com.kigya.foundation.utils.finiteShareIn
import com.kigya.foundation.views.BaseViewModel
import com.kigya.foundation.views.ResultFlow
import com.kigya.foundation.views.ResultMutableStateFlow
import com.kigya.quaso.R
import com.kigya.quaso.model.game.Game
import com.kigya.quaso.model.game.GameRepositoryImpl
import com.kigya.quaso.model.region.Region
import com.kigya.quaso.model.region.RegionsRepositoryImpl
import com.kigya.quaso.views.quiz.QuizFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample


class HomeViewModel(
    private val toasts: Toasts,
    private val navigator: Navigator,
    private val resources: Resources,
    private val regionRepository: RegionsRepositoryImpl,
    private val gameRepository: GameRepositoryImpl
) : BaseViewModel(), RegionsAdapter.Listener {

    private val _instantSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)
    private val _sampledSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)
    private val _availableRegions: ResultMutableStateFlow<List<Region>> = MutableStateFlow(
        PendingResult()
    )


    val viewState: ResultFlow<ViewState> = combine(
        _availableRegions,
        _instantSaveInProgress,
        _sampledSaveInProgress,
        ::mergeSources
    )

    val screenTitle: LiveData<String> = viewState
        .map { result ->
            return@map if (result is SuccessResult) {
                result.data.totalPointsTitle
            } else {
                resources.getString(R.string.nan)
            }
        }
        .asLiveData()

    init {
        load()
    }

    @OptIn(FlowPreview::class)
    override fun onRegionChosen(region: Region) {
        if (_instantSaveInProgress.value.isInProgress()) return
        onItemClicked(region)
    }

    @FlowPreview
    fun onItemClicked(region: Region) = viewModelScope.launch {
        try {
            _instantSaveInProgress.value = PercentageProgress.START
            _sampledSaveInProgress.value = PercentageProgress.START

            val game = Game(region, 0)
            val flow = gameRepository.setLatestGame(game).finiteShareIn(this)

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

            navigator.launch(QuizFragment.Screen(game))
        } catch (e: Exception) {
            if (e !is CancellationException)
                toasts.toast(resources.getString(R.string.error_happened))
        } finally {
            _instantSaveInProgress.value = EmptyProgress
            _sampledSaveInProgress.value = EmptyProgress
        }
    }

    private fun mergeSources(
        regions: Result<List<Region>>,
        instantSaveInProgress: Progress,
        sampledSaveInProgress: Progress
    ): Result<ViewState> {
        return regions.map { regionsList ->
            ViewState(
                regionsList,
                showLoadingProgressBar = instantSaveInProgress.isInProgress(),
                saveProgressPercentage = instantSaveInProgress.getPercentage(),
                saveProgressPercentageMessage = resources.getString(
                    R.string.percentage_value,
                    sampledSaveInProgress.getPercentage()
                ),
                totalPointsTitle = resources.getString(
                    R.string.points_value,
                    gameRepository.getTotalPoints(resources)
                )
            )
        }
    }

    private fun load() {
        into(_availableRegions) {
            delay(1000) // emitation of the first result is delayed to show the progress bar
            regionRepository.getAvailableRegions()
        }
    }

    fun tryAgain() = load()

    data class ViewState(
        val regionsList: List<Region>,
        val showLoadingProgressBar: Boolean,
        val saveProgressPercentage: Int,
        val saveProgressPercentageMessage: String,
        val totalPointsTitle: String
    )

}