package com.xfinitymobile.blueprint_architecture

import android.support.v7.app.ActionBar
import android.support.v7.widget.RecyclerView
import android.view.View
import com.xfinity.blueprint.presenter.ScreenPresenter
import com.xfinity.blueprint.view.ScreenViewDelegate

interface Architect<in T: ScreenPresenter<*>> {
    fun initBlueprint(layout: View, presenter: T, actionBar: ActionBar? = null)
}

abstract class DefaultArchitect<out T: DefaultScreenView>(private val screenViewDelegateFactory: ScreenViewDelegateFactory) :
        Architect<ScreenPresenter<T>> {

    lateinit var container: View
    private lateinit var loadingDots: View
    protected lateinit var recyclerView: RecyclerView
    lateinit var screenViewDelegate: ScreenViewDelegate
    var supportActionBar: ActionBar? = null

    override fun initBlueprint(layout: View, presenter: ScreenPresenter<T>, actionBar: ActionBar?) {
//        container = layout.findViewById(R.id.container)
//        loadingDots = layout.findViewById(R.id.loading_indicator)
//        recyclerView = layout.findViewById(R.id.recycler_view) as RecyclerView
        supportActionBar = actionBar

        screenViewDelegate = screenViewDelegateFactory.create(loadingDots)

        val screenView = getScreenView()
        presenter.attachView(screenView)
        recyclerView.adapter = screenView.screenViewDelegate.componentAdapter
    }

    abstract fun getScreenView() : T
}

//class DefaultScreenViewArchitect @Inject constructor(screenViewDelegateFactory: ScreenViewDelegateFactory)
//    : DefaultArchitect<DefaultScreenView>(screenViewDelegateFactory) {
//    override fun getScreenView(): DefaultScreenView = DefaultScreenView(screenViewDelegate, MessageView(container),
//            RecyclerViewBackgroundManager(recyclerView))
//}
//
//class CollapsingToolbarScreenViewArchitect @Inject constructor(screenViewDelegateFactory: ScreenViewDelegateFactory)
//    : DefaultArchitect<ToolbarScreenView>(screenViewDelegateFactory) {
//    private lateinit var collapsingAppBarLayout: CollapsingHeaderImageAppBarLayout
//
//    override fun initBlueprint(layout: View, presenter: ScreenPresenter<ToolbarScreenView>, actionBar: ActionBar?) {
//        collapsingAppBarLayout = layout.findViewById(R.id.header_image_toolbar) as CollapsingHeaderImageAppBarLayout
//        super.initBlueprint(layout, presenter, actionBar)
//    }
//
//    override fun getScreenView(): ToolbarScreenView {
//        return ToolbarScreenView(screenViewDelegate, SnackbarMessageView(container), supportActionBar,
//                collapsingAppBarLayout.headerImage, RecyclerViewBackgroundManager(recyclerView))
//    }
//}
//
//class ToolbarScreenViewArchitect @Inject constructor(screenViewDelegateFactory: ScreenViewDelegateFactory)
//    : DefaultArchitect<ToolbarScreenView>(screenViewDelegateFactory) {
//    override fun getScreenView(): ToolbarScreenView = ToolbarScreenView(screenViewDelegate, SnackbarMessageView(container), supportActionBar, null,
//            RecyclerViewBackgroundManager(recyclerView))
//}
//
//class ToolbarEventHandlingArchitect @Inject constructor(private val screenViewDelegateFactory: ScreenViewDelegateFactory) :
//        Architect<EventHandlingScreenPresenter<ToolbarEventHandlingScreenView>> {
//    override fun initBlueprint(layout: View, presenter: EventHandlingScreenPresenter<ToolbarEventHandlingScreenView>,
//                               actionBar: ActionBar?) {
//        val container = layout.findViewById(R.id.container) as View
//        val loadingDots = layout.findViewById(R.id.loading_dots) as View
//        val recyclerView = layout.findViewById(R.id.recycler_view) as RecyclerView
//
//        val screenViewDelegate = screenViewDelegateFactory.create(presenter, loadingDots)
//
//        val screenView = ToolbarEventHandlingScreenView(screenViewDelegate, SnackbarMessageView(container), actionBar,
//                RecyclerViewBackgroundManager(recyclerView))
//
//        presenter.attachView(screenView)
//
//        recyclerView.adapter = screenView.screenViewDelegate.componentAdapter
//    }
//}
//
//class EventHandlingArchitect @Inject constructor(private val screenViewDelegateFactory: ScreenViewDelegateFactory) :
//        Architect<EventHandlingScreenPresenter<DefaultEventHandlingScreenView>> {
//    override fun initBlueprint(layout: View, presenter: EventHandlingScreenPresenter<DefaultEventHandlingScreenView>,
//                               actionBar: ActionBar?) {
//        val container: View = layout.findViewById(R.id.container)
//        val loadingDots: View = layout.findViewById(R.id.loading_dots)
//        val recyclerView = layout.findViewById(R.id.recycler_view) as RecyclerView
//
//        val screenViewDelegate = screenViewDelegateFactory.create(presenter, loadingDots)
//
//        val screenView = DefaultEventHandlingScreenView(screenViewDelegate, SnackbarMessageView(container),
//                RecyclerViewBackgroundManager(recyclerView))
//
//        presenter.attachView(screenView)
//
//        recyclerView.adapter = screenView.screenViewDelegate.componentAdapter
//    }
//}