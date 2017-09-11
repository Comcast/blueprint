package com.xfinity.rmvp_sample.mvp.presenter

import com.xfinity.rmvp.event.ComponentEvent
import com.xfinity.rmvp.event.ComponentEventManager
import com.xfinity.rmvp.model.ComponentModel
import com.xfinity.rmvp.presenter.EventEmittingComponentPresenter
import com.xfinity.rmvp.view.ComponentView
import com.xfinity.rmvp_sample.mvp.model.DataItemModel
import com.xfinity.rmvp_sample.mvp.view.DataItemView

class DataItemPresenter(componentEventManager: ComponentEventManager) :
        EventEmittingComponentPresenter(componentEventManager) {
    override fun present(componentView: ComponentView<*>, componentModel: ComponentModel) {
        (componentView as DataItemView).setData((componentModel as DataItemModel).data)
    }

    override fun onComponentClicked(componentView: ComponentView<*>, position: Int) {
        if (componentView is DataItemView) {
            componentView.setData("Component $position was clicked")
        }

        componentEventManager.postEvent(DataItemClickedEvent("This is the event for position $position"))
    }


    data class DataItemClickedEvent(val toast: String) : ComponentEvent
}