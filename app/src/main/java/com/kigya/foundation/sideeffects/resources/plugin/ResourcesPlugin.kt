package com.kigya.foundation.sideeffects.resources.plugin

import android.content.Context
import com.kigya.foundation.sideeffects.SideEffectMediator
import com.kigya.foundation.sideeffects.SideEffectPlugin
import com.kigya.foundation.sideeffects.resources.Resources

/**
 * Plugin for accessing app resources from view-models.
 * Allows adding [Resources] interface to the view-model constructor.
 */
class ResourcesPlugin : SideEffectPlugin<ResourcesSideEffectMediator, Nothing> {

    override val mediatorClass: Class<ResourcesSideEffectMediator>
        get() = ResourcesSideEffectMediator::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<Nothing> {
        return ResourcesSideEffectMediator(applicationContext)
    }

}