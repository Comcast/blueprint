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