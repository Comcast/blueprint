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

package com.xfinity.blueprint_sample_library.mvp.presenter

import android.annotation.SuppressLint
import com.xfinity.blueprint.architecture.*
import com.xfinity.blueprint.event.ComponentEvent
import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint.model.Component
import com.xfinity.blueprint.presenter.ComponentEventHandler
import com.xfinity.blueprint_sample_library.ResourceProvider
import com.xfinity.blueprint_sample_library.blueprint.AppComponentRegistry
import com.xfinity.blueprint_sample_library.mvp.model.DataItemModel
import com.xfinity.blueprint_sample_library.mvp.model.DynamicScreenModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class ArchitectSamplePresenter(override val componentEventManager: ComponentEventManager,
                               private val resourceProvider: ResourceProvider) :
    ToolbarScreenPresenter(), ComponentEventHandler {

    var model: DynamicScreenModel = DynamicScreenModel()
    lateinit var view: ToolbarScreenView
    private val dataItemPresenter: DataItemPresenter = DataItemPresenter(componentEventManager)
    private var headerPosition = 0

    override fun attachView(screenView: ToolbarScreenView) {
        view = screenView
    }

    override fun resume() {
        super.resume()
        present()
    }

    /**
     * Present the overall screen, by adding Components
     */
    @SuppressLint("CheckResult")
    override fun present() {
        //present toolbar )
        view.hideToolbarBackButton()
        view.onActionItemSelectedBehavior = {
            onToolbarMenuItemClicked(it)
        }

        view.setOnRefreshBehavior {
            present()
            view.finishRefresh()
        }

        val screenComponents = mutableListOf<Component>()
        if (!model.headerModel.header.isEmpty()) {
            screenComponents.add(Component(model.headerModel, AppComponentRegistry.HeaderView_VIEW_TYPE))

            if (!model.headerModel.enabled) {
                screenComponents.add(Component(AppComponentRegistry.LoadingDotsView_VIEW_TYPE))

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
                    screenComponents.add(Component(dataItemModel, AppComponentRegistry.DataItemView_VIEW_TYPE,
                        dataItemPresenter))
                }
            }
        }


        if (model.headerModel.enabled && !model.footerModel.enabled) {
            screenComponents.add(Component(AppComponentRegistry.LoadingDotsView_VIEW_TYPE))

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

    private fun onToolbarMenuItemClicked(itemId: Int): Boolean {
        return when (itemId) {
            resourceProvider.ids.removeId -> {
                removeItemRequested()
                true
            }
            resourceProvider.ids.refreshDataItemsId -> {
                refreshDataItems()
                true
            }
            else -> false
        }
    }
}