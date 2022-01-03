package com.xfinity.blueprint.architecture

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

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
    fun setShowHomeAsUp(showHomeAsUp: Boolean)
    fun setUpIndicatorIcon(upIndicatorId: Int)
    fun setUpIndicatorIcon(upIndicator: Drawable)
}

interface ToolbarPresenter {
    fun attachToolbarView(toolbarView: ToolbarView?)
    fun presentToolbar()
}

abstract class DefaultToolbarPresenter: ToolbarPresenter {
    abstract var toolbarView: ToolbarView?
    override fun attachToolbarView(toolbarView: ToolbarView?) {
        this.toolbarView = toolbarView
    }
}