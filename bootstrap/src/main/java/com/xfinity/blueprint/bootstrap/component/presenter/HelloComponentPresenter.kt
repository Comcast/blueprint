package com.xfinity.blueprint.bootstrap.component.presenter

import com.xfinity.blueprint.bootstrap.component.model.SimpleTextModel
import com.xfinity.blueprint.bootstrap.component.view.HelloComponent
import com.xfinity.blueprint.presenter.ComponentPresenter
import com.xfinity.blueprint_annotations.DefaultPresenter

@DefaultPresenter(viewClass = HelloComponent::class)
class HelloComponentPresenter : ComponentPresenter<HelloComponent, SimpleTextModel> {
    override fun present(view: HelloComponent, model: SimpleTextModel) {
        view.setHelloText(model.text)
    }
}