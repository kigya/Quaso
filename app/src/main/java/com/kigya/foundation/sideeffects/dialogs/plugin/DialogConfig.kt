package com.kigya.foundation.sideeffects.dialogs.plugin

import com.kigya.foundation.sideeffects.dialogs.Dialogs

/**
 * Configuration of alert dialog displayed by [Dialogs.show]
 */
data class DialogConfig(
    val title: String,
    val message: String,
    val positiveButton: String = "",
    val negativeButton: String = "",
    val cancellable: Boolean = true
)