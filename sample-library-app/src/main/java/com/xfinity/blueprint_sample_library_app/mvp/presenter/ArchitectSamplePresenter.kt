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

import com.xfinity.blueprint.architecture.DefaultScreenView
import com.xfinity.blueprint.architecture.ToolbarPresenter
import com.xfinity.blueprint.architecture.ToolbarView
import com.xfinity.blueprint.event.ComponentEvent
import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint.model.Component
import com.xfinity.blueprint.presenter.ComponentEventHandler
import com.xfinity.blueprint.presenter.ScreenPresenter
import com.xfinity.blueprint_sample_library.blueprint.AppComponentRegistry.DataItemView_VIEW_TYPE
import com.xfinity.blueprint_sample_library.mvp.model.DataItemModel
import com.xfinity.blueprint_sample_library_app.R
import com.xfinity.blueprint_sample_library_app.ResourceProvider
import com.xfinity.blueprint_sample_library_app.blueprint.AppComponentRegistry
import com.xfinity.blueprint_sample_library_app.mvp.model.DynamicScreenModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class ArchitectSamplePresenter(override val componentEventManager: ComponentEventManager,
                               private val resourceProvider: ResourceProvider) :
        ScreenPresenter<DefaultScreenView>, ComponentEventHandler, ToolbarPresenter {

    var model: DynamicScreenModel = DynamicScreenModel()
    lateinit var view: DefaultScreenView
    private var toolbarView: ToolbarView? = null
    private val dataItemPresenter: DataItemPresenter = DataItemPresenter(componentEventManager)
    private var headerPosition = 0

    override fun attachView(screenView: DefaultScreenView) {
        view = screenView
    }

    override fun attachToolbarView(toolbarView: ToolbarView?) {
        this.toolbarView = toolbarView
    }

    override fun resume() {
        super.resume()
        present()
        presentToolbar()
    }

    /**
     * Present the overall screen, by adding Components
     */
    override fun present() {
        view.setOnRefreshBehavior {
            present()
            view.finishRefresh()
        }

        val screenComponents = mutableListOf<Component>()
        if (!model.headerModel.header.isEmpty()) {
            screenComponents.add(Component(model.headerModel, AppComponentRegistry.HeaderView_VIEW_TYPE))

            if (!model.headerModel.enabled) {
                screenComponents.add(Component(AppComponentRegistry.LoadingIndicator_VIEW_TYPE))

                Completable.complete().delay(3000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            model.headerModel.enabled = true
                            for (dataItemModel in model.dataItemModels) {
                                dataItemModel.enabled = true
                            }
                            view.setBackgroundColor(resourceProvider.colors.white)
                            present()
                            view.onComponentChanged(headerPosition)
                        }
            }
        }

        if (model.dataItemModels[0].enabled) {
            for (dataItemModel in model.dataItemModels) {
                if (dataItemModel.enabled) {
                    screenComponents.add(Component(dataItemModel, DataItemView_VIEW_TYPE,
                            dataItemPresenter))
                }
            }
        }


        if (model.headerModel.enabled && !model.footerModel.enabled) {
            screenComponents.add(Component(AppComponentRegistry.LoadingIndicator_VIEW_TYPE))

            Completable.complete().delay(3000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        model.footerModel.enabled = true
                        present()
                    }
        } else if (model.footerModel.enabled) {
            screenComponents.add(Component(model.footerModel, AppComponentRegistry.FooterView_VIEW_TYPE))
        }

        view.updateComponents(screenComponents)
    }

    override fun presentToolbar() {
        toolbarView?.onActionItemSelectedBehavior = { itemId ->
            when (itemId) {
                R.id.remove -> {
                    removeItemRequested()
                    true
                }
                R.id.refresh_data_items -> {
                    refreshDataItems()
                    true
                }
                else -> false
            }
        }
    }

    override fun onComponentEvent(componentEvent: ComponentEvent): Boolean {
        if (componentEvent is DataItemPresenter.DataItemClickedEvent) {
            view.showMessage(componentEvent.toast)
            return true  //consume
        }

        return false
    }

    private fun removeItemRequested() {
        if (model.dataItemModels.size > 0) {
            model.dataItemModels.removeAt(model.dataItemModels.size - 1)
        }
        present()
    }

    private fun refreshDataItems() {
        model.dataItemModels = mutableListOf(DataItemModel(), DataItemModel(), DataItemModel(),
                DataItemModel(), DataItemModel(), DataItemModel())
        for (dataItemModel in model.dataItemModels) {
            dataItemModel.enabled = true
        }
        present()
    }
}