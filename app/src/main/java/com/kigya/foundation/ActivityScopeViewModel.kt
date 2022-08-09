package com.kigya.foundation

import androidx.lifecycle.ViewModel
import com.kigya.foundation.sideeffects.SideEffectMediator
import com.kigya.foundation.sideeffects.SideEffectMediatorsHolder

/**
 * Holder for side-effect mediators.
 * It is based on activity view-model because instances of side-effect mediators
 * should be available from fragments' view-models (usually they are passed to the view-model constructor).
 */
class ActivityScopeViewModel : ViewModel() {

    internal val sideEffectMediatorsHolder = SideEffectMediatorsHolder()

    val sideEffectMediators: List<SideEffectMediator<*>>
        get() = sideEffectMediatorsHolder.mediators

    override fun onCleared() {
        super.onCleared()
        sideEffectMediatorsHolder.clear()
    }

}