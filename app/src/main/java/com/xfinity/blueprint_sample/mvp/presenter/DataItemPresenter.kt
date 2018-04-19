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

import com.xfinity.blueprint.event.ComponentEvent
import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint.model.ComponentModel
import com.xfinity.blueprint.presenter.EventEmittingComponentPresenter
import com.xfinity.blueprint.view.ComponentView
import com.xfinity.blueprint_sample.mvp.model.DataItemModel
import com.xfinity.blueprint_sample.mvp.view.DataItemView

class DataItemPresenter(componentEventManager: ComponentEventManager) :
        EventEmittingComponentPresenter(componentEventManager) {
    override fun present(componentView: ComponentView<*>, componentModel: ComponentModel) {
        (componentView as DataItemView).setDataText((componentModel as DataItemModel).data)
    }

    override fun onComponentClicked(componentView: ComponentView<*>, position: Int) {
        if (componentView is DataItemView) {
            componentView.setDataText("Component $position was clicked")
        }

        componentEventManager.postEvent(DataItemClickedEvent("This is the event for position $position"))
    }


    data class DataItemClickedEvent(val toast: String) : ComponentEvent
}