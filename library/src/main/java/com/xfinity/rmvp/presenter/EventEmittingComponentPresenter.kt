package com.xfinity.rmvp.presenter

import com.xfinity.rmvp.event.ComponentEventManager

abstract class EventEmittingComponentPresenter(val componentEventManager: ComponentEventManager) : ComponentPresenter