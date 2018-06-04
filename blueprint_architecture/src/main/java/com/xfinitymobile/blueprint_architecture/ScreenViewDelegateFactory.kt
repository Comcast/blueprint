package com.xfinitymobile.blueprint_architecture

import android.view.View
import com.xfinity.blueprint.ComponentRegistry
import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint.presenter.EventHandlingScreenPresenter
import com.xfinity.blueprint.view.EventHandlingScreenViewDelegate
import com.xfinity.blueprint.view.ScreenViewDelegate

class ScreenViewDelegateFactory(private val componentRegistry: ComponentRegistry,
                                private val componentEventManager: ComponentEventManager) {
    fun create(presenter: EventHandlingScreenPresenter<*>, loadingDots: View? = null): EventHandlingScreenViewDelegate =
            EventHandlingScreenViewDelegate(componentRegistry, componentEventManager, presenter, loadingDots)

    fun create(loadingDots: View? = null): ScreenViewDelegate =
            ScreenViewDelegate(componentRegistry, loadingDots)
}