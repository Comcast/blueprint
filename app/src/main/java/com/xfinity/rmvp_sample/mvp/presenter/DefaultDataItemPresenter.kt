package com.xfinity.rmvp_sample.mvp.presenter

import com.xfinity.rmvp.event.ComponentEventManager
import com.xfinity.rmvp.model.ComponentModel
import com.xfinity.rmvp.presenter.EventEmittingComponentPresenter
import com.xfinity.rmvp.view.ComponentView
import com.xfinity.rmvp_annotations.DefaultPresenter
import com.xfinity.rmvp_annotations.DefaultPresenterConstructor
import com.xfinity.rmvp_sample.mvp.model.DataItemModel
import com.xfinity.rmvp_sample.mvp.view.DataItemView

@DefaultPresenter(viewClass = DataItemView::class)
class DefaultDataItemPresenter
@DefaultPresenterConstructor constructor(componentEventManager: ComponentEventManager,
                                         val defaultDataItemName: String,
                                         val defaultDataItemId: Int) :
        EventEmittingComponentPresenter(componentEventManager) {
    override fun present(componentView: ComponentView<*>, componentModel: ComponentModel) {
        if ((componentModel as DataItemModel).data.isEmpty()) {
            (componentView as DataItemView).setData(defaultDataItemName + defaultDataItemId)
        } else {
            (componentView as DataItemView).setData(componentModel.data)
        }
    }

    override fun onComponentClicked(componentView: ComponentView<*>, position: Int) {
        if (componentView is DataItemView) {
            componentView.setData("Component $position was clicked")
        }

        componentEventManager.postEvent(DataItemPresenter.DataItemClickedEvent("default data item clicked"))
    }
}