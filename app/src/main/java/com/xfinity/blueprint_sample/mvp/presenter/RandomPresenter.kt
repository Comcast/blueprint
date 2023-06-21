package com.xfinity.blueprint_sample.mvp.presenter

import com.xfinity.blueprint.presenter.ComponentPresenter
import com.xfinity.blueprint_annotations.DefaultPresenter
import com.xfinity.blueprint_sample.mvp.model.RandomModel
import com.xfinity.blueprint_sample.mvp.view.RandomView

@DefaultPresenter(viewClass = RandomView::class)
class RandomPresenter: ComponentPresenter<RandomView, RandomModel>
{
    override fun present(view: RandomView, model: RandomModel) {
        view.setRandomtextText(model.randomtext)
        view.setEnabled(model.enabled)
    }
}