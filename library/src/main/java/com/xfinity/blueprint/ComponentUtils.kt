package com.xfinity.blueprint

import com.xfinity.blueprint.model.Component
import com.xfinity.blueprint.model.ComponentModel
import com.xfinity.blueprint.presenter.ComponentPresenter
import com.xfinity.blueprint.presenter.DefaultComponentPresenter

fun MutableList<Component>.addComponent(viewType: Int) {
    this.add(Component(object : ComponentModel {}, viewType, DefaultComponentPresenter()))
}

fun MutableList<Component>.addComponent(viewType: Int, model: ComponentModel) {
    this.add(Component(model, viewType))
}

fun MutableList<Component>.addComponent(viewType: Int, model: ComponentModel, presenter: ComponentPresenter?) {
    this.add(Component(model, viewType, presenter))
}