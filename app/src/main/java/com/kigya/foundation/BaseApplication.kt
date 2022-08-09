package com.kigya.foundation

import android.content.Context

/**
 * Base interface for application classes
 */
interface BaseApplication {

    /**
     * The list of singleton scope dependencies that can be added to the fragment
     * view-model constructors.
     */
    val singletonScopeDependencies: List<Any>

}