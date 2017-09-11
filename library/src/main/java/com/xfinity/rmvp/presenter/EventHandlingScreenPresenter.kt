package com.xfinity.rmvp.presenter

import com.xfinity.rmvp.event.ComponentEventListener
import com.xfinity.rmvp.view.EventHandlingScreenView

interface EventHandlingScreenPresenter<in T : EventHandlingScreenView> : ScreenPresenter<T>, ComponentEventListener