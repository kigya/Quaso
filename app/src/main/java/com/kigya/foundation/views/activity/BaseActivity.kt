package com.kigya.foundation.views.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kigya.foundation.sideeffects.SideEffectPluginsManager

/**
 * Base class to simplify the activity implementation.
 */
abstract class BaseActivity : AppCompatActivity(), ActivityDelegateHolder {

    private var _delegate: ActivityDelegate? = null
    override val delegate: ActivityDelegate
        get() = _delegate!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _delegate = ActivityDelegate(this).also {
            registerPlugins(it.sideEffectPluginsManager)
            it.onCreate(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        delegate.onSavedInstanceState(outState)
    }

    override fun onBackPressed() {
        if (!delegate.onBackPressed()) super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        _delegate = null
    }

    override fun onSupportNavigateUp(): Boolean {
        return delegate.onSupportNavigateUp() ?: super.onSupportNavigateUp()
    }

    abstract fun registerPlugins(manager: SideEffectPluginsManager)

}