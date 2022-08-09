package com.kigya.foundation.model

/**
 * Represents the progress state of some operation: whether progress should be displayed or not.
 */
sealed class Progress

/**
 * Progress shouldn't be displayed
 */
object EmptyProgress : Progress()

/**
 * Progress should be displayed and also may indicate the percentage value.
 */
data class PercentageProgress(
    val percentage: Int
) : Progress() {

    companion object {
        val START = PercentageProgress(percentage = 0)
    }

}

fun Progress.isInProgress() = this !is EmptyProgress

fun Progress.getPercentage() = (this as? PercentageProgress)?.percentage ?: PercentageProgress.START.percentage
