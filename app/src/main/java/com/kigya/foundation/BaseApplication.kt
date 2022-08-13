package com.kigya.foundation

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