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

package com.xfinity.blueprint_sample

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.xfinity.blueprint.architecture.*
import com.xfinity.blueprint.architecture.activity.ToolbarScreenViewActivity
import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint_sample.blueprint.AppComponentRegistry
import com.xfinity.blueprint_sample.mvp.presenter.CustomScreenViewPresenter
import com.xfinity.blueprint_sample.mvp.view.CustomScreenView

/**
 * Sample activity that demonstrates using the Blueprint Architecture Components
 */
class CustomLayoutArchitectSampleActivity : ToolbarScreenViewActivity<CustomScreenView>() {
    //Dependencies.  These would normally be injected
    private val componentEventManager = ComponentEventManager()
    private val componentRegistry = AppComponentRegistry(componentEventManager, defaultItemId, defaultItemName)
    private val resourceProvider: ResourceProvider by lazy { ResourceProvider(this) }

    //Custom Architect to use our custom screen view
    override val architect: DefaultArchitect<CustomScreenView> by lazy {
        object : DefaultArchitect<CustomScreenView>(componentRegistry) {
            override val screenView: CustomScreenView
                get() = CustomScreenView(fab, screenViewDelegate,
                    SnackbarMessageView(container), PullToRefreshView(ptrFrame),
                    RecyclerViewScreenManager(recyclerView))
        }
    }

    override val presenter: CustomScreenViewPresenter by lazy { CustomScreenViewPresenter(componentEventManager, resourceProvider)}
    override val toolbarPresenter: ToolbarPresenter by lazy { presenter }

    private var fab: FloatingActionButton? = null

    init {
        //set our custom layout and toolbar menu
        layoutId = R.layout.fab_toolbar_screen_view
        menuId = R.menu.main_menu
    }

    override fun onSetupComplete() {
        fab = findViewById(R.id.fab)
    }

    companion object {
        const val defaultItemName = "DefaultDataItemName"
        const val defaultItemId = 0
    }
}