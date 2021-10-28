/*
 * Copyright 2017 Comcast Cable Communications Management, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xfinity.blueprint.presenter

import com.xfinity.blueprint.event.ComponentEvent
import com.xfinity.blueprint.event.ComponentEventListener
import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint.view.ScreenView

interface ScreenPresenter<in T : ScreenView> : ComponentEventListener {
    val componentEventManager : ComponentEventManager

    fun attachView(screenView: T)
    fun present()

    fun resume() {
        componentEventManager.registerListener(this)
    }

    fun pause() {
        componentEventManager.unregisterListener(this)
    }

    override fun onComponentEvent(componentEvent: ComponentEvent): Boolean {
        return false
    }
}

abstract class DefaultScreenPresenter<in T : ScreenView> : ScreenPresenter<T>  {
    override val componentEventManager : ComponentEventManager = ComponentEventManager()
}