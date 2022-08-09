package com.kigya.quaso.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kigya.foundation.views.BaseFragment
import com.kigya.foundation.views.BaseScreen
import com.kigya.foundation.views.screenViewModel
import com.kigya.quaso.R
import com.kigya.quaso.databinding.FragmentHomeBinding
import com.kigya.quaso.views.collectFlow
import com.kigya.quaso.views.onTryAgain
import com.kigya.quaso.views.renderSimpleResult

class HomeFragment : BaseFragment() {

    class Screen : BaseScreen

    override val viewModel by screenViewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        val adapter = RegionsAdapter(viewModel)

        collectFlow(viewModel.viewState) { result ->
            renderSimpleResult(binding.root, result) { viewState ->
                with(binding) {
                    setupRecyclerView(adapter, viewState)
                    setupProgress(viewState)
                }
            }
        }

        onTryAgain(binding.root, viewModel::tryAgain)

        return binding.root
    }

    private fun FragmentHomeBinding.setupProgress(viewState: HomeViewModel.ViewState) {
        saveProgressGroup.visibility =
            if (viewState.showLoadingProgressBar) View.VISIBLE else View.GONE
        saveProgressBar.progress = viewState.saveProgressPercentage
        savingPercentageTextView.text = viewState.saveProgressPercentageMessage
    }

    private fun FragmentHomeBinding.setupRecyclerView(
        adapter: RegionsAdapter,
        viewState: HomeViewModel.ViewState
    ) {
        adapter.items = viewState.regionsList
        regionsRecyclerView.adapter = adapter
    }
}


