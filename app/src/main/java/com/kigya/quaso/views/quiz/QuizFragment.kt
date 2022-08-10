package com.kigya.quaso.views.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
            overlayList = getShuffledOverlayList()

            collectFlow(viewModel.viewState) { result ->
                renderSimpleResult(binding.root, result) { viewState ->
                    setFlag(viewState)
                    setRegionTitleText()
                    notifyUpdates()
                    setNextButtonVisibility(viewState)
                }
            }
        }
        binding.nextButton.setOnClickListener { viewModel.onAttemptUsed(overlayList) }
        return binding.root
    }

    private fun FragmentQuizBinding.setNextButtonVisibility(viewState: QuizFragmentViewModel.ViewState) {
        if (viewState.showNextButton) {
            nextButton.visibility = View.VISIBLE
        } else {
            nextButton.visibility = View.INVISIBLE
        }
    }

    private fun notifyUpdates() = viewModel.hideOverlayItem(overlayList)

    private fun FragmentQuizBinding.setRegionTitleText() {
        quizStatusText.text =
            getString(
                R.string.quizTitle,
                viewModel.region.toString()
            )
    }

    private fun FragmentQuizBinding.getShuffledOverlayList() = listOf(
        topLeftOverlay,
        topCenterOverlay,
        topRightOverlay,
        bottomLeftOverlay,
        bottomCenterOverlay,
        bottomRightOverlay
    ).shuffled()

    private fun FragmentQuizBinding.setFlag(viewState: QuizFragmentViewModel.ViewState) {
        flag.setImageResource(viewState.countriesList.first().bigFlag)
    }
}
