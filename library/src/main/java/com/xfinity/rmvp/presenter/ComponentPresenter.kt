package com.xfinity.rmvp.presenter

import com.xfinity.rmvp.model.ComponentModel
import com.xfinity.rmvp.view.ComponentView

interface ComponentPresenter {
    fun present(componentView: ComponentView<*>, componentModel: ComponentModel)
    fun onComponentClicked(componentView: ComponentView<*>, position: Int)
}