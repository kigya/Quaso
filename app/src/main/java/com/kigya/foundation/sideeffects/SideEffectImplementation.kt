package com.kigya.foundation.sideeffects

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Base class for side-effect implementations.
 * Implementations are responsible for the real implementation of side-effects taking into
 * account activity lifecycle.
 */
abstract class SideEffectImplementation {

    private lateinit var activity: AppCompatActivity

    fun requireActivity(): AppCompatActivity = activity

    open fun onCreate(savedInstanceState: Bundle?) = Unit
    open fun onBackPressed(): Boolean {
        return false
    }

    open fun onRequestUpdates() = Unit
    open fun onSupportNavigateUp(): Boolean? = null
    open fun onSaveInstanceState(outBundle: Bundle) = Unit

    internal fun injectActivity(activity: AppCompatActivity) {
        this.activity = activity
    }

}