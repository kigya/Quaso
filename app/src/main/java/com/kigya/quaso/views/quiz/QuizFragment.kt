package com.kigya.quaso.views.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.kigya.foundation.views.BaseFragment
import com.kigya.foundation.views.BaseScreen
import com.kigya.foundation.views.screenViewModel
import com.kigya.quaso.R
import com.kigya.quaso.databinding.FragmentQuizBinding
import com.kigya.quaso.model.game.Game
import com.kigya.quaso.views.collectFlow
import com.kigya.quaso.views.renderSimpleResult


class QuizFragment : BaseFragment() {

    class Screen(
        val game: Game
    ) : BaseScreen

    override val viewModel by screenViewModel<QuizFragmentViewModel>()

    private lateinit var overlayList: List<ImageView>

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
                    notifyUpdates()
                }
            }
        }
        setTransitions()

        binding.changeChoiseButton.setOnClickListener { viewModel.onChangePressed() }
        binding.hintButton.setOnClickListener { viewModel.showHintDialog() }
        binding.countryPlaceholder.text = getString(viewModel.getCurrentChoise())
        binding.nextButton.setOnClickListener { viewModel.onSkipPressed() }
        binding.backIcon.setOnClickListener { viewModel.onReturnPressed() }

        return binding.root
    }

    private fun setTransitions() {
        this.apply {
            enterTransition = viewModel.getEnterTransition()
            exitTransition = viewModel.getExitTranstion()
            reenterTransition = viewModel.getReenterTransition()
        }
    }

    private fun FragmentQuizBinding.notifyUpdates() {
        viewModel.onTrigger(overlayList)
        topProgressBar.setProgressPercentage(viewModel.getProgressPercentage())
    }

    private fun FragmentQuizBinding.setRegionTitleText() {
        quizStatusText.text = getString(
            R.string.quizTitle,
            viewModel.region.toString()
        )
    }

    private fun FragmentQuizBinding.getOverlayList() = listOf(
        topLeftOverlay,
        topCenterOverlay,
        topRightOverlay,
        bottomLeftOverlay,
        bottomCenterOverlay,
        bottomRightOverlay
    )

    private fun FragmentQuizBinding.setFlag(viewState: QuizFragmentViewModel.ViewState) {
        val country = viewState.country
        flag.setImageResource(country.bigFlag)
    }
}
