package com.kigya.foundation.sideeffects.resources

import android.content.SharedPreferences
import androidx.annotation.StringRes
import com.kigya.foundation.sideeffects.resources.plugin.ResourcesPlugin

/**
 * Interface for accessing resources from view-models.
 * You need to add [ResourcesPlugin] to your activity before using this feature.
 */
interface Resources {

    fun getString(@StringRes resId: Int, vararg args: Any): String

    fun getSharedPreferences(key: String, mode: Int): SharedPreferences

}