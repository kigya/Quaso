package com.kigya.foundation.sideeffects.resources

import android.content.SharedPreferences
import androidx.annotation.StringRes
import com.kigya.foundation.sideeffects.resources.plugin.ResourcesPlugin

/**
 * Interface for accessing resources from view-models.
 */
interface Resources {

    fun getString(@StringRes resId: Int, vararg args: Any): String

    fun getSharedPreferences(key: String, mode: Int): SharedPreferences

}