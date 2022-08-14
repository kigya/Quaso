package com.kigya.quaso.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kigya.foundation.views.BaseFragment
import com.kigya.foundation.views.BaseScreen
import com.kigya.foundation.views.screenViewModel
import com.kigya.quaso.databinding.FragmentHomeBinding
import com.kigya.quaso.views.collectFlow
import com.kigya.quaso.views.onTryAgain
import com.kigya.quaso.views.renderSimpleResult

/**
 * Home screen fragment.
 */
class HomeFragment : BaseFragment() {

    /**
     * When launching a fragment, no params will be passed.
     */
    class Screen : BaseScreen

    /**
     * ViewModel creation using custom delegate.
     */
    override val viewModel by screenViewModel<HomeViewModel>()

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        val adapter = RegionsAdapter(viewModel)

        with(binding) {
            collectFlow(viewModel.viewState) { result ->
                renderSimpleResult(binding.root, result) { viewState ->
                    setupRecyclerView(adapter, viewState)
                    setupProgress(viewState)
                    setupLevel(viewState)
                }
            }
            viewModel.totalPoints.observe(viewLifecycleOwner) {
                pointsStatus.text = it
            }
            viewModel.latestMode.observe(viewLifecycleOwner) {
                latestPlayModeText.text = it
            }
            viewModel.latestPoints.observe(viewLifecycleOwner) {
                latestPointsText.text = it
            }
            setupLevelCircularParogressBar()
            onTryAgain(root, viewModel::tryAgain)
        }
        setFragmentTransition()

        onTryAgain(binding.root) { viewModel.tryAgain() }

        return binding.root
    }


    /**
     * Setup fragment with animations.
     */
    private fun setFragmentTransition() {
        this.apply { exitTransition = viewModel.getExitTransition() }
    }

    /**
     * Provide circular progress bar by actual progress.
     */
    private fun FragmentHomeBinding.setupLevelCircularParogressBar() {
        circularProgressBar.apply {
            setProgressWithAnimation(
                progress = viewModel.getCurrentPercentage(),
                duration = CIRCULAR_PROGRESS_DURATION
            )
        }
    }

    /**
     * Setup [HomeFragment] current user level.
     */
    private fun FragmentHomeBinding.setupLevel(viewState: HomeViewModel.ViewState) {
        levelTextView.text = viewState.currentLevel.toString()
    }

    /**
     * Setup [HomeFragment] progress bar when loading.
     */
    private fun FragmentHomeBinding.setupProgress(viewState: HomeViewModel.ViewState) {
        saveProgressGroup.visibility =
            if (viewState.showLoadingProgressBar) View.VISIBLE else View.GONE
        saveProgressBar.progress = viewState.saveProgressPercentage
        savingPercentageTextView.text = viewState.saveProgressPercentageMessage
    }

    /**
     * Setup [HomeFragment] recycler view.
     */
    private fun FragmentHomeBinding.setupRecyclerView(
        adapter: RegionsAdapter,
        viewState: HomeViewModel.ViewState
    ) {
        adapter.items = viewState.regionsList
        regionsRecyclerView.adapter = adapter
    }

    /**
     * [HomeFragment] constants.
     */
    companion object {
        private const val CIRCULAR_PROGRESS_DURATION = 2000L
    }
}


