package com.xfinity.rmvp.view

import com.xfinity.rmvp.event.ComponentEvent
import com.xfinity.rmvp.event.ComponentEventListener
import com.xfinity.rmvp.event.ComponentEventManager
import com.xfinity.rmvp.presenter.EventHandlingScreenPresenter

interface EventHandlingScreenView : ScreenView, ComponentEventListener {
    val componentEventManager : ComponentEventManager
    val presenter : EventHandlingScreenPresenter<*>

    fun resume() {
        componentEventManager.registerListener(this)
    }

    fun pause() {
        componentEventManager.unregisterListener(this)
    }

    override fun onComponentEvent(componentEvent: ComponentEvent): Boolean {
        return presenter.onComponentEvent(componentEvent)
    }
}