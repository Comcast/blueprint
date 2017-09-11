package com.xfinity.rmvp_sample.mvp.presenter

import com.xfinity.rmvp.model.ComponentModel
import com.xfinity.rmvp.presenter.ComponentPresenter
import com.xfinity.rmvp.view.ComponentView
import com.xfinity.rmvp_annotations.DefaultPresenter
import com.xfinity.rmvp_sample.mvp.model.HeaderModel
import com.xfinity.rmvp_sample.mvp.view.HeaderView

@DefaultPresenter(viewClass = HeaderView::class)
class HeaderPresenter : ComponentPresenter {
    override fun present(componentView: ComponentView<*>, componentModel: ComponentModel) {
        (componentView as HeaderView).setEnabled((componentModel as HeaderModel).enabled)

        if (componentModel.enabled) {
            componentView.setEnabled(true)
            componentView.setHeader(componentModel.header)
        }
    }

    override fun onComponentClicked(componentView: ComponentView<*>, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}