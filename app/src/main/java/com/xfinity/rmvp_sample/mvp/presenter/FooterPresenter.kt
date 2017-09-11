package com.xfinity.rmvp_sample.mvp.presenter

import com.xfinity.rmvp_sample.mvp.model.FooterModel
import com.xfinity.rmvp_sample.mvp.view.FooterView
import com.xfinity.rmvp.model.ComponentModel
import com.xfinity.rmvp.presenter.ComponentPresenter
import com.xfinity.rmvp.view.ComponentView
import com.xfinity.rmvp_annotations.DefaultPresenter

@DefaultPresenter(viewClass = FooterView::class)
class FooterPresenter : ComponentPresenter {
    override fun present(componentView: ComponentView<*>, componentModel: ComponentModel) {
        (componentView as FooterView).setFooter((componentModel as FooterModel).footer)
    }

    override fun onComponentClicked(componentView: ComponentView<*>, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}