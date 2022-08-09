package com.kigya.foundation.sideeffects.toasts.plugin

import android.content.Context
import android.widget.Toast
import com.kigya.foundation.utils.MainThreadExecutor
import com.kigya.foundation.sideeffects.SideEffectMediator
import com.kigya.foundation.sideeffects.toasts.Toasts

/**
 * Android implementation of [Toasts]. Displaying simple toast message and getting string from resources.
 */
class ToastsSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<Nothing>(), Toasts {

    private val executor = MainThreadExecutor()

    override fun toast(message: String) {
        executor.execute {
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
        }
    }

}