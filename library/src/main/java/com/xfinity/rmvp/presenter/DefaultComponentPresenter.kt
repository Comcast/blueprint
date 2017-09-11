package com.xfinity.rmvp.presenter

import com.xfinity.rmvp.model.ComponentModel
import com.xfinity.rmvp.view.ComponentView

open class DefaultComponentPresenter : ComponentPresenter {
    override fun present(componentView: ComponentView<*>, componentModel: ComponentModel) {}
    override fun onComponentClicked(componentView: ComponentView<*>, position: Int) {}
}