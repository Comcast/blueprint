package com.xfinity.blueprint.architecture

import android.graphics.drawable.Drawable
import android.view.Menu
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBar
import com.xfinity.blueprint.view.ScreenView
import com.xfinity.blueprint.view.ScreenViewDelegate

open class ToolbarScreenView(screenViewDelegate: ScreenViewDelegate, messageView: MessageView, refreshHandler: RefreshHandler,
                             screenManager: ScreenManager, private val actionBar: ActionBar?) :
        DefaultScreenView(screenViewDelegate, messageView, refreshHandler, screenManager), ToolbarView {
    var menu: Menu? = null

    override var onActionItemSelectedBehavior: (Int) -> Boolean = { false }
    override var onToolbarBackButtonClickedBehavior: () -> Boolean = { false }

    override fun setToolbarActionItemIcon(itemId: Int, iconId: Int) {
        menu?.findItem(itemId)?.setIcon(iconId)
    }

    override fun setToolbarActionItemIsVisible(itemId: Int, isVisible: Boolean) {
        menu?.findItem(itemId)?.isVisible = isVisible
    }

    override fun setToolbarTitle(title: CharSequence) {
        actionBar?.title = title
    }

    override fun hideToolbarBackButton() {
        actionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun showToolbarBackButton() {
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun setToolbarIcon(@DrawableRes resId: Int) {
        actionBar?.setIcon(resId)
    }

    override fun showToolBar() {
        actionBar?.show()
    }

    override fun hideToolBar() {
        actionBar?.hide()
    }

    override fun setShowHomeAsUp(showHomeAsUp: Boolean) {
        actionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
    }

    override fun setUpIndicatorIcon(upIndicatorId: Int) {
        actionBar?.setHomeAsUpIndicator(upIndicatorId)
    }

    override fun setUpIndicatorIcon(upIndicator: Drawable) {
        actionBar?.setHomeAsUpIndicator(upIndicator)
    }
}

interface MessageHandlingScreenView : ScreenView, MessageView, RefreshHandler

/**
 * ScreenView which also implements error handling, messaging and refresh
 */
open class DefaultScreenView(val screenViewDelegate: ScreenViewDelegate,
                             private val errorView: MessageView,
                             private val refreshHandler: RefreshHandler,
                             private val screenManager: ScreenManager) :
        MessageHandlingScreenView,
        ScreenView by screenViewDelegate,
        MessageView by errorView,
        RefreshHandler by refreshHandler,
        ScreenManager by screenManager
