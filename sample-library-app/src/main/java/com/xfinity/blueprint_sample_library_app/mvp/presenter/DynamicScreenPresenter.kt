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

package com.xfinity.blueprint_sample_library_app.mvp.presenter

import com.xfinity.blueprint.event.ComponentEvent
import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint.model.Component
import com.xfinity.blueprint.model.ComponentModel
import com.xfinity.blueprint.presenter.DefaultComponentPresenter
import com.xfinity.blueprint.presenter.EventHandlingScreenPresenter
import com.xfinity.blueprint_sample.blueprint.AppComponentRegistry
import com.xfinity.blueprint_sample.mvp.model.DataItemModel
import com.xfinity.blueprint_sample.mvp.model.DynamicScreenModel
import com.xfinity.blueprint_sample.mvp.view.DefaultDynamicScreenView
import java.util.Timer
import java.util.TimerTask


class DynamicScreenPresenter(override val componentEventManager: ComponentEventManager) :
        EventHandlingScreenPresenter<DefaultDynamicScreenView> {

    var model: DynamicScreenModel = DynamicScreenModel()
    lateinit var view: DefaultDynamicScreenView
    private val dataItemPresenter: DataItemPresenter = DataItemPresenter(componentEventManager)
    var headerPosition = 0

    override fun attachView(screenView: DefaultDynamicScreenView) {
        view = screenView
    }

    /**
     * Present the overall screen, by adding Components
     */
    override fun present() {
        val screenComponents = mutableListOf<Component>()
        if (!model.headerModel.header.isEmpty()) {
            screenComponents.add(Component(model.headerModel, AppComponentRegistry.HeaderView_VIEW_TYPE))

            if (!model.headerModel.enabled) {
                screenComponents.add(Component(object : ComponentModel{},
                        AppComponentRegistry.LoadingDotsView_VIEW_TYPE, DefaultComponentPresenter()))
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        model.headerModel.enabled = true
                        for (dataItemModel in model.dataItemModels) {
                            dataItemModel.enabled = true
                        }

                        view.runOnUiThread (Runnable {
                            view.setEnabled(true)
                            present()
                            view.onComponentChanged(headerPosition)
                        })
                    }
                }, 3000)
            }
        }

        if (model.dataItemModels[0].enabled) {
            for (dataItemModel in model.dataItemModels) {
                if (dataItemModel.enabled) {
                    screenComponents.add(Component(dataItemModel, AppComponentRegistry.DataItemView_VIEW_TYPE,
                            dataItemPresenter))
                }
            }
        }


        if (model.headerModel.enabled && !model.footerModel.enabled) {
            screenComponents.add(Component(object : ComponentModel{},
                    AppComponentRegistry.LoadingDotsView_VIEW_TYPE, DefaultComponentPresenter()))
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    model.footerModel.enabled = true
                    view.runOnUiThread (Runnable {
                        present()
                    })
                }
            }, 3000)
        } else if (model.footerModel.enabled) {
            screenComponents.add(Component(model.footerModel, AppComponentRegistry.FooterView_VIEW_TYPE))
        }

        view.updateComponents(screenComponents)
    }

    override fun onComponentEvent(componentEvent: ComponentEvent): Boolean {
        if (componentEvent is DataItemPresenter.DataItemClickedEvent) {
            view.toast(componentEvent.toast)
            return true  //consume
        }

        return false
    }

    fun removeItemRequested() {
        if (model.dataItemModels.size > 0) {
            model.dataItemModels.removeAt(model.dataItemModels.size - 1)
        }
        present()
    }

    fun refreshDataItems() {
        model.dataItemModels = mutableListOf(DataItemModel(), DataItemModel(), DataItemModel(),
                DataItemModel(), DataItemModel(), DataItemModel())
        for (dataItemModel in model.dataItemModels) {
            dataItemModel.enabled = true
        }
        present()
    }
}