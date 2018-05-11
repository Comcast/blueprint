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

import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint.presenter.EventEmittingComponentReflectionPresenter
import com.xfinity.blueprint_annotations.DefaultPresenter
import com.xfinity.blueprint_annotations.DefaultPresenterConstructor
import com.xfinity.blueprint_sample.mvp.model.DataItemModel
import com.xfinity.blueprint_sample.mvp.view.DataItemView

@DefaultPresenter(viewClass = DataItemView::class)
class DefaultDataItemPresenter
@DefaultPresenterConstructor constructor(componentEventManager: ComponentEventManager,
                                         private val dataItemName: String,
                                         private val dataItemId: Int) :
        EventEmittingComponentReflectionPresenter<DataItemView, DataItemModel>(componentEventManager) {

    override fun presentView(view: DataItemView, model: DataItemModel) {
        view.setData(if (model.data.isNotEmpty()) model.data else dataItemName + dataItemId)
    }

    override fun onComponentViewClicked(view: DataItemView, position: Int) {
        view.setData("Component $position was clicked")
        componentEventManager.postEvent(DataItemPresenter.DataItemClickedEvent("default data item clicked"))
    }
}