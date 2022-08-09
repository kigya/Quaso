package com.kigya.quaso.views.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentQuizBinding.inflate(inflater, container, false)

        collectFlow(viewModel.viewState) { result ->
            renderSimpleResult(binding.root, result) { viewState ->
                binding.flag.setImageDrawable(
                    this.context?.let {
                        ContextCompat.getDrawable(
                            it.applicationContext, // Context
                            viewState.countriesList[0].country.bigFlag // Drawable
                        )
                    })
            }
        }

        with(binding) {
            quizStatusText.text = getString(R.string.quizTitle, "tranerName", 1);
        }
        return binding.root
    }
}
