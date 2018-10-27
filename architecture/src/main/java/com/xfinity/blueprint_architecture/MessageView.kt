package com.xfinity.blueprint_architecture

import android.support.design.widget.Snackbar
import android.view.View

class SnackbarMessageView(private val anchorView: View) : MessageView {

    private var snackbar: Snackbar? = null

    override fun showMessage(msg: String,
                             actionLabel: String?,
                             actionLabelClickedBehavior: (() -> Unit)?,
                             duration: MessageDuration) {

        snackbar = Snackbar.make(anchorView, msg, duration.length)
        if (actionLabel != null && actionLabelClickedBehavior != null) {
            snackbar?.setAction(actionLabel, { actionLabelClickedBehavior.invoke() })
        }
        snackbar?.view?.announceForAccessibility(msg)
        snackbar?.show()
    }

    override fun hideMessage() {
        snackbar?.dismiss()
    }
}

enum class MessageDuration(val length: Int) {
    SHORT(Snackbar.LENGTH_SHORT),
    LONG(Snackbar.LENGTH_LONG),
    INDEFINITE(Snackbar.LENGTH_INDEFINITE)
}

interface MessageView {
    fun showMessage(msg: String,
                    actionLabel: String? = null,
                    actionLabelClickedBehavior: (() -> Unit)? = null,
                    duration: MessageDuration = MessageDuration.INDEFINITE)

    fun hideMessage()
}