package com.kigya.quaso.views.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.kigya.foundation.views.BaseFragment
import com.kigya.foundation.views.BaseScreen
import com.kigya.foundation.views.screenViewModel
import com.kigya.quaso.R
import com.kigya.quaso.databinding.FragmentQuizBinding
import com.kigya.quaso.model.game.Game
import com.kigya.quaso.views.collectFlow
import com.kigya.quaso.views.renderSimpleResult

/**
 * Quiz screen fragment.
 */
class QuizFragment : BaseFragment() {

    /**
     * When launching a fragment, the [Game] parameter will be passed.
     */
    class Screen(
        val game: Game
    ) : BaseScreen

    /**
     * ViewModel creation using custom delegate.
     */
    override val viewModel by screenViewModel<QuizFragmentViewModel>()

    /**
     * Holder for [ImageView] overlay cards.
     */
    private lateinit var overlayList: List<ImageView>

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentQuizBinding.inflate(inflater, container, false)

        with(binding) {
            overlayList = getOverlayList()

            collectFlow(viewModel.viewState) { result ->
                renderSimpleResult(binding.root, result) { viewState ->
                    setFlag(viewState)
                    setRegionTitleText()
                    loadUpdates()
                }
            }
            changeChoiseButton.setOnClickListener { viewModel.onChangePressed() }
            hintButton.setOnClickListener { viewModel.showHintDialog() }
            nextButton.setOnClickListener { viewModel.onSkipPressed() }
            backIcon.setOnClickListener { viewModel.onReturnPressed() }
            countryPlaceholder.text = getString(viewModel.getCurrentChoise())
        }
        setTransitions()

        return binding.root
    }

    /**
     * Transitions to switch between fragments.
     */
    private fun setTransitions() {
        this.apply {
            enterTransition = viewModel.getEnterTransition()
            exitTransition = viewModel.getExitTranstion()
            reenterTransition = viewModel.getReenterTransition()
        }
    }

    /**
     * Load updates during view creation.
     */
    private fun FragmentQuizBinding.loadUpdates() {
        viewModel.onTrigger(overlayList)
        topProgressBar.setProgressPercentage(viewModel.getProgressPercentage())
    }

    /**
     * Setter for top region title depending on selected game mode.
     */
    private fun FragmentQuizBinding.setRegionTitleText() {
        quizStatusText.text = getString(
            R.string.quizTitle,
            viewModel.region.toString()
        )
    }

    /**
     * Getting the list of overlay cards.
     */
    private fun FragmentQuizBinding.getOverlayList() = listOf(
        topLeftOverlay,
        topCenterOverlay,
        topRightOverlay,
        bottomLeftOverlay,
        bottomCenterOverlay,
        bottomRightOverlay
    )

    /**
     * Setting up flag display.
     */
    private fun FragmentQuizBinding.setFlag(viewState: QuizFragmentViewModel.ViewState) {
        val country = viewState.country
        flag.setImageResource(country.bigFlag)
    }
}
