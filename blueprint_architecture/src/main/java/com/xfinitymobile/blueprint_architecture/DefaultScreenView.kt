package com.xfinitymobile.blueprint_architecture

import android.support.annotation.DrawableRes
import android.support.v7.app.ActionBar
import android.widget.ImageView
import com.xfinity.blueprint.view.EventHandlingScreenView
import com.xfinity.blueprint.view.EventHandlingScreenViewDelegate
import com.xfinity.blueprint.view.ScreenView
import com.xfinity.blueprint.view.ScreenViewDelegate

/**
 * EventHandlingScreenView which also implements default message handling
 */
open class DefaultEventHandlingScreenView(val screenViewDelegate: EventHandlingScreenViewDelegate,
                                          private val messageView: MessageView) :
        EventAndMessageHandlingScreenView,
        EventHandlingScreenView by screenViewDelegate,
        MessageView by messageView,

class ToolbarEventHandlingScreenView(screenViewDelegate: EventHandlingScreenViewDelegate,
                                     messageView: MessageView,
                                     private val actionBar: ActionBar?) :
        DefaultEventHandlingScreenView(screenViewDelegate, messageView) {

    fun setTitle(title: CharSequence) {
        actionBar?.title = title
    }
}

class ToolbarScreenView(screenViewDelegate: ScreenViewDelegate, messageView: MessageView,
                        private val actionBar: ActionBar?, private val headerImage: ImageView? = null) :
        DefaultScreenView(screenViewDelegate, messageView) {

    fun setTitle(title: CharSequence) {
        actionBar?.title = title
    }


    fun setHeaderImage(@DrawableRes resId: Int) {
        headerImage?.setImageResource(resId)
    }

    fun hideBackButton() {
        actionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun setIcon(@DrawableRes resId: Int) {
        actionBar?.setIcon(resId)
    }
}


interface MessageHandlingScreenView : ScreenView, MessageView
interface EventAndMessageHandlingScreenView : EventHandlingScreenView, MessageView

/**
 * ScreenView which also implements default error handling
 */
open class DefaultScreenView(val screenViewDelegate: ScreenViewDelegate,
                             private val errorView: MessageView) :
        MessageHandlingScreenView,
        ScreenView by screenViewDelegate,
        MessageView by errorView
