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

import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint.presenter.ComponentPresenter
import com.xfinity.blueprint_annotations.DefaultPresenter
import com.xfinity.blueprint_annotations.DefaultPresenterConstructor
import com.xfinity.blueprint_sample_library.mvp.model.DataItemModel
import com.xfinity.blueprint_sample_library.mvp.view.DataItemView

@DefaultPresenter(viewClass = DataItemView::class)
class DefaultDataItemPresenter
@DefaultPresenterConstructor constructor(val componentEventManager: ComponentEventManager,
                                         private val defaultDataItemName: String,
                                         private val defaultDataItemId: Int) :
        ComponentPresenter<DataItemView, DataItemModel> {

    override fun present(view: DataItemView, model: DataItemModel) {

        if (model.data.isEmpty()) {
            view.setDataText(defaultDataItemName + defaultDataItemId)
        } else {
            view.setDataText(model.data)
        }

        view.setBehavior { position ->
            view.setDataText("Component $position was clicked")
            componentEventManager.postEvent(DataItemPresenter.DataItemClickedEvent("default data item clicked"))
        }
    }
}