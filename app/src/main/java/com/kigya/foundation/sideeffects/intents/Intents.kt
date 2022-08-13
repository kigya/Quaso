package com.kigya.foundation.sideeffects.intents

import com.kigya.foundation.sideeffects.intents.plugin.IntentsPlugin

/**
 * Side-effects interface for launching some system activities.
 */
interface Intents {

    /**
     * Open system settings for this application.
     */
    fun openAppSettings()

}