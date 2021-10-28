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

package com.xfinity.blueprint_sample_library

import com.xfinity.blueprint.architecture.ToolbarScreenViewArchitect
import com.xfinity.blueprint.architecture.activity.ToolbarScreenViewActivity
import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint_sample_library.blueprint.AppComponentRegistry
import com.xfinity.blueprint_sample_library.mvp.presenter.ArchitectSamplePresenter

/**
 * Sample activity that demonstrates using the Blueprint Architecture Components
 */
class ArchitectSampleActivity : ToolbarScreenViewActivity() {
    //Dependencies.  These would normally be injected
    private val componentEventManager = ComponentEventManager()
    private val componentRegistry = AppComponentRegistry(componentEventManager, defaultItemId, defaultItemName)
    private val resourceProvider: ResourceProvider by lazy { ResourceProvider(this) }

    //If you needed to use a ScreenView subclass, you would create your own Architect to use it.  Otherwise, you can
    // use the default architect
    override var architect: ToolbarScreenViewArchitect = ToolbarScreenViewArchitect(componentRegistry)

    override val presenter: ArchitectSamplePresenter by lazy {
        ArchitectSamplePresenter(componentEventManager, resourceProvider)
    }

    init {
        menuId = R.menu.main_menu
    }

    companion object {
        const val defaultItemName = "DefaultDataItemName"
        const val defaultItemId = 0
    }
}
