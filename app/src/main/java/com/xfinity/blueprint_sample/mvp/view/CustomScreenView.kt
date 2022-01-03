package com.xfinity.blueprint_sample.mvp.view

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.xfinity.blueprint.architecture.*
import com.xfinity.blueprint.view.ScreenViewDelegate

class CustomScreenView(private val fab: FloatingActionButton?, screenViewDelegate: ScreenViewDelegate,
                                       errorView: MessageView, refreshHandler: RefreshHandler, screenManager: ScreenManager) :
    DefaultScreenView(screenViewDelegate, errorView, refreshHandler, screenManager), FabView {

    override fun setFabOnClickedBehavior(behavior: () -> Unit) {
        fab?.setOnClickListener { behavior.invoke() }
    }

    override fun setFabSource(sourceId: Int) {
        fab?.setImageResource(sourceId)
    }
}

interface FabView {
    fun setFabOnClickedBehavior(behavior: () -> Unit)
    fun setFabSource(sourceId: Int)
}