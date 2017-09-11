package com.xfinity.rmvp.view

import android.view.View
import com.xfinity.rmvp.ComponentAdapter
import com.xfinity.rmvp.ComponentRegistry
import com.xfinity.rmvp.event.ComponentEventManager
import com.xfinity.rmvp.presenter.EventHandlingScreenPresenter

class EventHandlingScreenViewDelegate(componentRegistry: ComponentRegistry,
                                      override val componentEventManager: ComponentEventManager,
                                      override val presenter: EventHandlingScreenPresenter<*>,
                                      loadingView: View? = null,
                                      val screenViewDelegate: ScreenViewDelegate = ScreenViewDelegate(componentRegistry, loadingView)) :
        EventHandlingScreenView, ScreenView by screenViewDelegate {
    fun getComponentAdapter() : ComponentAdapter {
        return screenViewDelegate.componentAdapter
    }
}