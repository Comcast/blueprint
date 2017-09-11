package com.xfinity.rmvp.event

interface ComponentEvent

interface ComponentEventListener {
    fun onComponentEvent(componentEvent: ComponentEvent) : Boolean
}

interface ComponentEventEmitter {
    fun postComponentEvent(componentEvent: ComponentEvent)
}
