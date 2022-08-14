package com.kigya.foundation.sideeffects.toasts

/**
 * Interface for showing toast messages to the user from view-models.
 */
interface Toasts {

    /**
     * Display a simple toast message.
     */
    fun toast(message: String)

}