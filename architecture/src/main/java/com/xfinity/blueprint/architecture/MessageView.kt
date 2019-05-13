package com.xfinity.blueprint.architecture

import com.google.android.material.snackbar.Snackbar
import android.view.View

class SnackbarMessageView(private val anchorView: View) : MessageView {

    private var snackbar: Snackbar? = null

    override fun showMessage(msg: String,
                             actionLabel: String?,
                             actionLabelClickedBehavior: (() -> Unit)?,
                             duration: MessageDuration) {

        snackbar = Snackbar.make(anchorView, msg, duration.length())
        if (actionLabel != null && actionLabelClickedBehavior != null) {
            snackbar?.setAction(actionLabel) { actionLabelClickedBehavior.invoke() }
        }
        snackbar?.view?.announceForAccessibility(msg)
        snackbar?.show()
    }

    override fun hideMessage() {
        snackbar?.dismiss()
    }
}

fun MessageDuration.length(): Int {
    return when (this) {
        MessageDuration.SHORT -> Snackbar.LENGTH_SHORT
        MessageDuration.LONG -> Snackbar.LENGTH_LONG
        MessageDuration.INDEFINITE -> Snackbar.LENGTH_INDEFINITE
    }
}

enum class MessageDuration {
    SHORT,
    LONG,
    INDEFINITE
}

interface MessageView {
    fun showMessage(msg: String,
                    actionLabel: String? = null,
                    actionLabelClickedBehavior: (() -> Unit)? = null,
                    duration: MessageDuration = MessageDuration.INDEFINITE)

    fun hideMessage()
}