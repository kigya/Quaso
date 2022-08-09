package com.kigya.foundation.sideeffects.intents.plugin

import android.content.Context
import com.kigya.foundation.sideeffects.SideEffectMediator
import com.kigya.foundation.sideeffects.SideEffectPlugin
import com.kigya.foundation.sideeffects.intents.Intents

/**
 * Plugin for launching system activities from view-models.
 * Allows adding [Intents] interface to the view-model constructor.
 */
class IntentsPlugin : SideEffectPlugin<Intents, Nothing> {

    override val mediatorClass: Class<Intents>
        get() = Intents::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<Nothing> {
        return IntentsSideEffectMediator(applicationContext)
    }

}