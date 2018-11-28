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

package com.xfinity.blueprint.model

import com.xfinity.blueprint.presenter.ComponentPresenter
import com.xfinity.blueprint.presenter.DefaultComponentPresenter

interface ComponentModel

/**
 * StaticComponent are Components that have no Model or Presentation, just a layout that needs to be inflated and added.
 * Having a Model with no data will cause the DiffUtil Callback to always flag a Component as "new" and re-add it whenever
 * the screen is presented, which can cause blinking and other UI artifacts.  We'll use StaticComponent to add a default
 * Model that contains the viewType so that these types of Components are ignored in the Diff calculation.
 */
data class StaticComponentModel(val viewType: Int) : ComponentModel

data class Component(val model: ComponentModel,
                     val viewType: Int,
                     val presenter: ComponentPresenter<*, *>? = null) {

    /**
     *  Alternate constructor for creating components that are just a static view, with no presentation necessary.
     *  Meant for things like dividers, headers, footers, etc.
     */
    constructor(viewType: Int) : this(StaticComponentModel(viewType), viewType, DefaultComponentPresenter())
}