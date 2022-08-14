package com.kigya.quaso.activity

import androidx.lifecycle.viewModelScope
import com.kigya.foundation.views.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            delay(SPLASH_DELAY)
            _isLoading.value = false
        }
    }

    companion object {
        private const val SPLASH_DELAY = 1500L
    }
}