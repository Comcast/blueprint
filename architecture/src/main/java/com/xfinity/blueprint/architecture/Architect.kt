package com.xfinity.blueprint.architecture

import `in`.srain.cube.views.ptr.PtrClassicFrameLayout
import android.view.View
import androidx.appcompat.app.ActionBar
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
    private lateinit var loadingIndicator: View
    protected lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    lateinit var screenViewDelegate: ScreenViewDelegate
    var ptrFrame: PtrClassicFrameLayout? = null
    var supportActionBar: ActionBar? = null

    abstract val screenView : T

    override fun initBlueprint(layout: View, presenter: ScreenPresenter<T>, actionBar: ActionBar?) {
        container = layout.findViewById(R.id.container)
        loadingIndicator = layout.findViewById(R.id.loading_indicator)
        recyclerView = layout.findViewById(R.id.recycler_view) as androidx.recyclerview.widget.RecyclerView
        ptrFrame = layout.findViewById(R.id.ptr_frame)
        screenViewDelegate = ScreenViewDelegate(componentRegistry, loadingIndicator, recyclerView)

        presenter.attachView(screenView)
        recyclerView.adapter = screenView.screenViewDelegate.componentAdapter
    }
}

class DefaultScreenViewArchitect(componentRegistry: ComponentRegistry)
    : DefaultArchitect<DefaultScreenView>(componentRegistry) {
    override val screenView: DefaultScreenView by lazy {
        DefaultScreenView(screenViewDelegate,
            SnackbarMessageView(container), PullToRefreshView(ptrFrame),
            RecyclerViewScreenManager(recyclerView))
    }
}