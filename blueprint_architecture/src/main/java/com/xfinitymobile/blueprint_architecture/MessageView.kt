package com.xfinitymobile.blueprint_architecture

interface MessageView {
    fun showMessage(msg: String,
                    actionLabel: String? = null,
                    actionLabelClickedBehavior: (() -> Unit)? = null,
                    duration: MessageDuration = MessageDuration.INDEFINITE)

    fun hideMessage()
}

enum class MessageDuration {
    SHORT,
    LONG,
    INDEFINITE
}