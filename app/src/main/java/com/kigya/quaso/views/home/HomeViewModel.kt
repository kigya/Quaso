package com.kigya.quaso.views.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.transition.MaterialFadeThrough
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

/**
 * ViewModel which manages [HomeFragment] screen.
 */
class HomeViewModel(
    private val toasts: Toasts,
    private val navigator: Navigator,
    private val resources: Resources,
    private val regionRepository: RegionsRepositoryImpl,
    private val gameRepository: GameRepositoryImpl
) : BaseViewModel(), RegionsAdapter.Listener {

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
    private val _availableRegions: ResultMutableStateFlow<List<Region>> = MutableStateFlow(
        PendingResult()
    )

    /**
     * Main destination (contains merged values from [_availableRegions], [_instantSaveInProgress] & [_sampledSaveInProgress])
     */
    val viewState: ResultFlow<ViewState> = combine(
        _availableRegions,
        _instantSaveInProgress,
        _sampledSaveInProgress,
        ::mergeSources
    )

    /**
     * Total points [LiveData] to display.
     */
    val totalPoints: LiveData<String> = viewState
        .map { result ->
            return@map if (result is SuccessResult) result.data.totalPointsTitle
            else resources.getString(R.string.nan)
        }.asLiveData()

    /**
     * Latest points [LiveData] to display.
     */
    val latestPoints: LiveData<String> = viewState
        .map { result ->
            return@map if (result is SuccessResult) result.data.latestPoints
            else resources.getString(R.string.nan)
        }.asLiveData()

    /**
     * Latest mode [LiveData] to display.
     */
    val latestMode: LiveData<String> = viewState
        .map { result ->
            return@map if (result is SuccessResult) result.data.latestMode
            else resources.getString(R.string.nan)
        }.asLiveData()

    /**
     * Init block is run at every time the class is instantiated.
     */
    init {
        load()
    }

    /**
     * Called when user taps on a region.
     */
    @OptIn(FlowPreview::class)
    override fun onRegionChosen(region: Region) {
        if (_instantSaveInProgress.value.isInProgress()) return
        onItemClicked(region)
    }

    /**
     * Actions to perform when user taps on a region.
     */
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

    /**
     * Transformation pure method for combining data from several input flows:
     * - the result of fetching regions list (Result<List<Region>>)
     * - [Progress] instance which indicates whether saving operation is in
     *   progress or not
     *
     * All values above are merged into one [ViewState] instance:
     * ```
     * Flow<Result<List<Region>>> ----------+|
     * Flow<Progress> -----------------------|--> Flow<Result<ViewState>>
     * ```
     */
    private fun mergeSources(
        regions: Result<List<Region>>,
        instantSaveInProgress: Progress,
        sampledSaveInProgress: Progress
    ): Result<ViewState> {
        with(gameRepository) {
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
                    ),
                    latestPoints = getLatestPoints(resources).toString(),
                    latestMode = getLatestMode(resources),
                    currentLevel = getLevel(getTotalPoints(resources)).first
                )
            }
        }
    }

    /**
     * Loads regions list from repository.
     */
    private fun load() {
        into(_availableRegions) {
            delay(1000) // emitation of the first result is delayed to show the progress bar
            regionRepository.getAvailableRegions()
        }
    }

    /**
     * On error do the same as on [load] method (try to load again).
     */
    fun tryAgain() = load()

    /**
     * Add-on over exit fragment transition.
     */
    fun getExitTransition() = MaterialFadeThrough().apply { duration = 500 }

    /**
     * Get current level percentage ration between total points and required
     * point to reach next level.
     */
    fun getCurrentPercentage(): Float {
        val totalPoints = gameRepository.getTotalPoints(resources)
        return (totalPoints * 100f) / getLevel(totalPoints).second
    }

    /**
     * Getting the current level and required points to reach next level as [Pair]
     */
    private fun getLevel(totalPoints: Int): Pair<Int, Int> {
        when (totalPoints) {
            in 0..LEVEL_2 -> return 1 to LEVEL_2
            in (LEVEL_2 + 1)..LEVEL_3 -> return 2 to LEVEL_3
            in (LEVEL_3 + 1)..LEVEL_4 -> return 3 to LEVEL_4
            in (LEVEL_4 + 1)..LEVEL_5 -> return 4 to LEVEL_5
            in (LEVEL_5 + 1)..LEVEL_6 -> return 5 to LEVEL_6
            in (LEVEL_6 + 1)..LEVEL_7 -> return 6 to LEVEL_7
            in (LEVEL_7 + 1)..LEVEL_8 -> return 7 to LEVEL_8
            in (LEVEL_8 + 1)..LEVEL_9 -> return 8 to LEVEL_9
            in (LEVEL_9 + 1)..LEVEL_10 -> return 9 to LEVEL_10
            in (LEVEL_10 + 1)..LEVEL_11 -> return 10 to LEVEL_11
            in (LEVEL_11 + 1)..LEVEL_12 -> return 11 to LEVEL_12
            in (LEVEL_12 + 1)..LEVEL_13 -> return 12 to LEVEL_13
            in (LEVEL_13 + 1)..LEVEL_14 -> return 13 to LEVEL_14
            in (LEVEL_14 + 1)..LEVEL_15 -> return 14 to LEVEL_15
            in (LEVEL_15 + 1)..LEVEL_16 -> return 15 to LEVEL_16
            in (LEVEL_16 + 1)..LEVEL_17 -> return 16 to LEVEL_17
            in (LEVEL_17 + 1)..LEVEL_18 -> return 17 to LEVEL_18
            in (LEVEL_18 + 1)..LEVEL_19 -> return 18 to LEVEL_19
            in (LEVEL_19 + 1)..LEVEL_20 -> return 19 to LEVEL_20
            else -> return 20 to LEVEL_20
        }
    }

    /**
     * ViewState class which is used to combine data from several input flows
     */
    data class ViewState(
        val regionsList: List<Region>,
        val showLoadingProgressBar: Boolean,
        val saveProgressPercentage: Int,
        val saveProgressPercentageMessage: String,
        val totalPointsTitle: String,
        val latestPoints: String,
        val latestMode: String,
        val currentLevel: Int
    )

    /**
     * ViewModel constants.
     */
    companion object {
        private const val LEVEL_2 = 30
        private const val LEVEL_3 = 70
        private const val LEVEL_4 = 120
        private const val LEVEL_5 = 200
        private const val LEVEL_6 = 300
        private const val LEVEL_7 = 400
        private const val LEVEL_8 = 500
        private const val LEVEL_9 = 600
        private const val LEVEL_10 = 700
        private const val LEVEL_11 = 800
        private const val LEVEL_12 = 900
        private const val LEVEL_13 = 1000
        private const val LEVEL_14 = 1500
        private const val LEVEL_15 = 2000
        private const val LEVEL_16 = 2500
        private const val LEVEL_17 = 3000
        private const val LEVEL_18 = 3500
        private const val LEVEL_19 = 4000
        private const val LEVEL_20 = 4500
    }
}