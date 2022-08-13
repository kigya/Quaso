package com.kigya.foundation.views

import java.io.Serializable

/**
 * Base class for defining screen arguments.
 */
interface BaseScreen : Serializable {

    companion object {
        const val ARG_SCREEN = "ARG_SCREEN"
    }

}
