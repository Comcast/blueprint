/*
 *
 *  * Copyright 2018 Comcast Cable Communications Management, LLC
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.xfinity.blueprint_bootstrap

import com.xfinity.blueprint.architecture.DefaultArchitect
import com.xfinity.blueprint.architecture.DefaultScreenView
import com.xfinity.blueprint.architecture.DefaultScreenViewArchitect
import com.xfinity.blueprint.architecture.ToolbarPresenter
import com.xfinity.blueprint.architecture.activity.ToolbarScreenViewActivity
import com.xfinity.blueprint.presenter.ScreenPresenter
import com.xfinity.blueprint_bootstrap.screen.presenter.ToolbarScreenPresenter
import javax.inject.Inject

class MainToolbarActivity : ToolbarScreenViewActivity<DefaultScreenView>() {
    @Inject lateinit var defaultArchitect: DefaultScreenViewArchitect
    @Inject lateinit var mainPresenter: ToolbarScreenPresenter

    override val architect: DefaultArchitect<DefaultScreenView> by lazy { defaultArchitect }
    override val presenter: ScreenPresenter<DefaultScreenView> by lazy { mainPresenter }
    override val toolbarPresenter: ToolbarPresenter by lazy { mainPresenter }

    init {
        menuId = R.menu.toolbar_actions
    }
}