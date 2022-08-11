package com.kigya.quaso.views.choise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.kigya.foundation.views.BaseFragment
import com.kigya.foundation.views.BaseScreen
import com.kigya.foundation.views.screenViewModel
import com.kigya.quaso.databinding.FragmentSelectCountryBinding
import com.kigya.quaso.views.collectFlow
import com.kigya.quaso.views.onTryAgain
import com.kigya.quaso.views.renderSimpleResult

class SelectCountryFragment : BaseFragment() {

    class Screen : BaseScreen

    override val viewModel by screenViewModel<SelectCountryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSelectCountryBinding.inflate(inflater, container, false)
        val adapter = CountriesAdapter(viewModel, viewModel.resources)

        with(binding) {
            collectFlow(viewModel.viewState) { result ->
                renderSimpleResult(binding.root, result) { viewState ->
                    setupRecyclerView(adapter, viewState)
                    setupProgress(viewState)
                }
            }
            onTryAgain(root, viewModel::tryAgain)
        }

        return binding.root
    }

    private fun FragmentSelectCountryBinding.setupProgress(viewState: SelectCountryViewModel.ViewState) {
        saveProgressGroup.visibility =
            if (viewState.showLoadingProgressBar) View.VISIBLE else View.GONE
        saveProgressBar.progress = viewState.saveProgressPercentage
        savingPercentageTextView.text = viewState.saveProgressPercentageMessage
    }

    private fun FragmentSelectCountryBinding.setupRecyclerView(
        adapter: CountriesAdapter,
        viewState: SelectCountryViewModel.ViewState
    ) {
        adapter.items = viewState.countries
        countriesRecyclerView.adapter = adapter
    }
}