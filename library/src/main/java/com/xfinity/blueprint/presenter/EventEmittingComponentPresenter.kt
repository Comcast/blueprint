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

import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint.model.ComponentModel
import com.xfinity.blueprint.view.ComponentView

@Deprecated("Any Component that has access to the ComponentEventManager can send events. Don't use a subclass for this.")
interface EventEmittingComponentPresenter<V : ComponentView<*>, M : ComponentModel> : ComponentPresenter<V, M> {
    val componentEventManager: ComponentEventManager
}