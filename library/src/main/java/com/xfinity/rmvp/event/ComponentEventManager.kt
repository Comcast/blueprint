package com.xfinity.rmvp.event

class ComponentEventManager {
    val listeners = mutableListOf<ComponentEventListener>()

    fun registerListener(componentEventListener: ComponentEventListener) {
        listeners.add(componentEventListener)
    }

    fun unregisterListener(componentEventListener: ComponentEventListener) {
        listeners.remove(componentEventListener)
    }

    fun postEvent(componentEvent: ComponentEvent) {
        listeners.forEach {
            if (it.onComponentEvent(componentEvent)) {
                return@forEach
            }
        }
    }
}