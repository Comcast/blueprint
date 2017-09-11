package com.xfinity.rmvp_sample.mvp.presenter

import com.xfinity.rmvp.model.Component
import com.xfinity.rmvp.presenter.ScreenPresenter
import com.xfinity.rmvp.view.ScreenView
import com.xfinity.rmvp_sample.mvp.model.StaticScreenModel
import com.xfinity.rmvp_sample.rmvp.AppComponentRegistry

class StaticScreenPresenter : ScreenPresenter<ScreenView> {
    var model: StaticScreenModel = StaticScreenModel()
    lateinit var screenView: ScreenView

    override fun attachView(screenView: ScreenView) {
        this.screenView = screenView
    }

    /**
     * Present the overall screen, by adding Components
     */
    override fun present() {
        val screenComponents = mutableListOf<Component>()
        screenComponents.add(Component(model.headerModel, AppComponentRegistry.HeaderView_VIEW_TYPE))
        model.dataItemModels.forEach {
            screenComponents.add(Component(it, AppComponentRegistry.DataItemView_VIEW_TYPE))
        }

        screenComponents.add(Component(model.footerModel, AppComponentRegistry.FooterView_VIEW_TYPE))
        screenView.updateComponents(screenComponents)
    }
}