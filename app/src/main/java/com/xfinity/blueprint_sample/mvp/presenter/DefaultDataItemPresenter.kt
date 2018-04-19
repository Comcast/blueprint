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
import com.xfinity.blueprint.model.ComponentModel
import com.xfinity.blueprint.presenter.EventEmittingComponentPresenter
import com.xfinity.blueprint.view.ComponentView
import com.xfinity.blueprint_annotations.DefaultPresenter
import com.xfinity.blueprint_annotations.DefaultPresenterConstructor
import com.xfinity.blueprint_sample.mvp.model.DataItemModel
import com.xfinity.blueprint_sample.mvp.view.DataItemView

@DefaultPresenter(viewClass = DataItemView::class)
class DefaultDataItemPresenter
@DefaultPresenterConstructor constructor(componentEventManager: ComponentEventManager,
                                         val defaultDataItemName: String,
                                         val defaultDataItemId: Int) :
        EventEmittingComponentPresenter(componentEventManager) {
    override fun present(componentView: ComponentView<*>, componentModel: ComponentModel) {
        if ((componentModel as DataItemModel).data.isEmpty()) {
            (componentView as DataItemView).setDataText(defaultDataItemName + defaultDataItemId)
        } else {
            (componentView as DataItemView).setDataText(componentModel.data)
        }
    }

    override fun onComponentClicked(componentView: ComponentView<*>, position: Int) {
        if (componentView is DataItemView) {
            componentView.setDataText("Component $position was clicked")
        }

        componentEventManager.postEvent(DataItemPresenter.DataItemClickedEvent("default data item clicked"))
    }
}