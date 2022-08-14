package com.kigya.foundation.sideeffects.dialogs.plugin

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleObserver
import com.kigya.foundation.model.SuccessResult
import com.kigya.foundation.sideeffects.SideEffectImplementation
import com.kigya.foundation.sideeffects.dialogs.plugin.DialogsSideEffectMediator.DialogRecord
import com.kigya.foundation.sideeffects.dialogs.plugin.DialogsSideEffectMediator.RetainedState

class DialogsSideEffectImpl(
    private val retainedState: RetainedState
) : SideEffectImplementation(), LifecycleObserver {

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().lifecycle.addObserver(this)
    }

    fun onStart() {
        val record = retainedState.record ?: return
        showDialog(record)
    }

    fun onStop() {
        removeDialog()
    }

    fun showDialog(record: DialogRecord) {
        val config = record.config
        val emitter = record.emitter
        val builder = AlertDialog.Builder(requireActivity())
            .setTitle(config.title)
            .setMessage(config.message)
            .setCancelable(config.cancellable)
        if (config.positiveButton.isNotBlank()) {
            builder.setPositiveButton(config.positiveButton) { _, _ ->
                emitter.emit(SuccessResult(true))
                dialog = null
            }
        }
        if (config.negativeButton.isNotBlank()) {
            builder.setNegativeButton(config.negativeButton) { _, _ ->
                emitter.emit(SuccessResult(false))
                dialog = null
            }
        }
        if (config.cancellable) {
            builder.setOnCancelListener {
                emitter.emit(SuccessResult(false))
                dialog = null
            }
        }
        val dialog = builder.create()
        dialog.show()
        this.dialog = dialog
    }

    fun removeDialog() {
        dialog?.dismiss()
        dialog = null
    }

}