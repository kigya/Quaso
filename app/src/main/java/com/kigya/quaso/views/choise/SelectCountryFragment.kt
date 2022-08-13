package com.kigya.quaso.views.choise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kigya.foundation.views.BaseFragment
import com.kigya.foundation.views.BaseScreen
import com.kigya.foundation.views.screenViewModel
import com.kigya.quaso.databinding.FragmentSelectCountryBinding
import com.kigya.quaso.model.game.Game
import com.kigya.quaso.views.collectFlow
import com.kigya.quaso.views.onTryAgain
import com.kigya.quaso.views.renderSimpleResult

/**
 * Country selection screen fragment.
 */
class SelectCountryFragment : BaseFragment() {

    /**
     * When launching a fragment, no params will be passed.
     */
    class Screen : BaseScreen

    /**
     * ViewModel creation using custom delegate.
     */
    override val viewModel by screenViewModel<SelectCountryViewModel>()

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSelectCountryBinding.inflate(inflater, container, false)

        with(binding) {
            collectFlow(viewModel.viewState) { result ->
                renderSimpleResult(binding.root, result) { viewState ->
                    setupRecyclerView(viewState)
                    setupProgress(viewState)
                }
            }
            searchView.setOnQueryTextListener(viewModel)
            backIcon.setOnClickListener { viewModel.onReturnPressed() }
            onTryAgain(root, viewModel::tryAgain)
        }
        setTransitions()

        return binding.root
    }

    /**
     * Setup fragment transitions.
     */
    private fun setTransitions() {
        this.apply {
            enterTransition = viewModel.getEnterTransition()
            returnTransition = viewModel.getReturnTransition()
        }
    }

    /**
     * Setup loading progress.
     */
    private fun FragmentSelectCountryBinding.setupProgress(viewState: SelectCountryViewModel.ViewState) {
        saveProgressGroup.visibility =
            if (viewState.showLoadingProgressBar) View.VISIBLE else View.GONE
        saveProgressBar.progress = viewState.saveProgressPercentage
        savingPercentageTextView.text = viewState.saveProgressPercentageMessage
    }

    /**
     * Setup recycler view.
     */
    private fun FragmentSelectCountryBinding.setupRecyclerView(
        viewState: SelectCountryViewModel.ViewState
    ) {
        with(viewModel) {
            adapter.itemsFiltered = viewState.countries
            adapter.items = viewState.countries
            countriesRecyclerView.adapter = adapter
        }
    }
}