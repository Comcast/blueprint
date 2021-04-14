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

package com.xfinity.blueprint_sample.mvp.presenter

import com.xfinity.blueprint.model.Component
import com.xfinity.blueprint.presenter.ScreenPresenter
import com.xfinity.blueprint.view.ScreenView
import com.xfinity.blueprint_sample.blueprint.AppComponentRegistry
import com.xfinity.blueprint_sample.mvp.model.StaticScreenModel

class StaticScreenPresenter : ScreenPresenter<ScreenView> {
    var model: StaticScreenModel = StaticScreenModel()
    lateinit var screenView: ScreenView

    override fun attachView(screenView: ScreenView) {
        this.screenView = screenView
    }

    /**
     * Present the overall screen, by adding Components
     */
    override fun present() {
        val screenComponents = mutableListOf<Component>()
        screenComponents.add(Component(model.headerModel, AppComponentRegistry.HeaderView_VIEW_TYPE))
        model.dataItemModels.forEach {
            screenComponents.add(Component(it, AppComponentRegistry.DataItemView_VIEW_TYPE))
        }

        screenComponents.add(Component(model.footerModel, AppComponentRegistry.FooterView_VIEW_TYPE))
        screenView.updateComponents(screenComponents)
    }
}