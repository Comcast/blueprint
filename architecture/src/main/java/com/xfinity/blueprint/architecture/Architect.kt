package com.xfinity.blueprint.architecture

import `in`.srain.cube.views.ptr.PtrClassicFrameLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.RecyclerView
import android.view.View
import com.xfinity.blueprint.ComponentRegistry
import com.xfinity.blueprint.presenter.ScreenPresenter
import com.xfinity.blueprint.view.ScreenViewDelegate

interface Architect<in T : ScreenPresenter<*>> {
    val componentRegistry: ComponentRegistry
    fun initBlueprint(layout: View, presenter: T, actionBar: ActionBar? = null)
}

abstract class DefaultArchitect<out T : DefaultScreenView>(override val componentRegistry: ComponentRegistry)
    : Architect<ScreenPresenter<T>> {
    lateinit var container: View
    private lateinit var loadingDots: View
    protected lateinit var recyclerView: RecyclerView
    lateinit var screenViewDelegate: ScreenViewDelegate
    var ptrFrame: PtrClassicFrameLayout? = null
    var supportActionBar: ActionBar? = null

    override fun initBlueprint(layout: View, presenter: ScreenPresenter<T>, actionBar: ActionBar?) {
        container = layout.findViewById(R.id.container)
        loadingDots = layout.findViewById(R.id.loading_dots)
        recyclerView = layout.findViewById(R.id.recycler_view) as RecyclerView
        ptrFrame = layout.findViewById(R.id.ptr_frame)
        supportActionBar = actionBar

        screenViewDelegate = ScreenViewDelegate(componentRegistry, loadingDots)

        val screenView = getScreenView()
        presenter.attachView(screenView)
        recyclerView.adapter = screenView.screenViewDelegate.componentAdapter
    }

    abstract fun getScreenView(): T
}

class DefaultScreenViewArchitect(componentRegistry: ComponentRegistry)
    : DefaultArchitect<DefaultScreenView>(componentRegistry) {
    override fun getScreenView(): DefaultScreenView = DefaultScreenView(screenViewDelegate,
            SnackbarMessageView(container), PullToRefreshView(ptrFrame), RecyclerViewScreenManager(recyclerView))
}

class ToolbarScreenViewArchitect(componentRegistry: ComponentRegistry)
    : DefaultArchitect<ToolbarScreenView>(componentRegistry) {
    override fun getScreenView(): ToolbarScreenView = ToolbarScreenView(screenViewDelegate,
            SnackbarMessageView(container), PullToRefreshView(ptrFrame), RecyclerViewScreenManager(recyclerView),
            supportActionBar)
}