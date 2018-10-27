package com.xfinity.blueprint_architecture

import android.support.annotation.DrawableRes
import android.support.v7.app.ActionBar
import com.xfinity.blueprint.view.ScreenView
import com.xfinity.blueprint.view.ScreenViewDelegate

open class ToolbarScreenView(screenViewDelegate: ScreenViewDelegate, messageView: MessageView, refreshHandler: RefreshHandler,
                        screenManager: ScreenManager, private val actionBar: ActionBar?) :
        DefaultScreenView(screenViewDelegate, messageView, refreshHandler, screenManager) {

    fun setTitle(title: CharSequence) {
        actionBar?.title = title
    }

    fun hideBackButton() {
        actionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun setIcon(@DrawableRes resId: Int) {
        actionBar?.setIcon(resId)
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
