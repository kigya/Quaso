package com.kigya.foundation.sideeffects.dialogs

import com.kigya.foundation.sideeffects.dialogs.plugin.DialogConfig
import com.kigya.foundation.sideeffects.dialogs.plugin.DialogsPlugin

/**
 * Side-effects interface for managing dialogs from view-model.
 */
interface Dialogs {

    /**
     * Show alert dialog to the user and wait for the user choice.
     */
    suspend fun show(dialogConfig: DialogConfig): Boolean

}