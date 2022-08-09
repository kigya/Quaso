package com.kigya.foundation.sideeffects.resources.plugin

import android.content.Context
import android.content.SharedPreferences
import com.kigya.foundation.sideeffects.SideEffectMediator
import com.kigya.foundation.sideeffects.resources.Resources

class  ResourcesSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<Nothing>(), Resources {

    override fun getString(resId: Int, vararg args: Any): String {
        return appContext.getString(resId, *args)
    }

    override fun getSharedPreferences(key: String, mode: Int): SharedPreferences {
        return appContext.getSharedPreferences(key, mode)
    }
}