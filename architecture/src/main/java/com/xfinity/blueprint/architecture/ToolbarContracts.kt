package com.xfinity.blueprint.architecture

import androidx.annotation.DrawableRes
import com.xfinity.blueprint.presenter.ScreenPresenter

interface ToolbarView {
    var onActionItemSelectedBehavior: (Int) -> Boolean
    var onToolbarBackButtonClickedBehavior: () -> Boolean
    fun setToolbarActionItemIcon(itemId: Int, iconId: Int)
    fun setToolbarActionItemIsVisible(itemId: Int, isVisible: Boolean)
    fun setToolbarTitle(title: CharSequence)
    fun hideToolbarBackButton()
    fun showToolbarBackButton()
    fun setToolbarIcon(@DrawableRes resId: Int)
    fun showToolBar()
    fun hideToolBar()
}

abstract class ToolbarScreenPresenter : ScreenPresenter<ToolbarScreenView> {
    private var screenView: ToolbarScreenView? = null
    override fun attachView(screenView: ToolbarScreenView) {
        this.screenView = screenView
    }
}