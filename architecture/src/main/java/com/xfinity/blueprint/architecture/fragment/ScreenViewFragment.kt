package com.xfinity.blueprint.architecture.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.xfinity.blueprint.architecture.*
import com.xfinity.blueprint.architecture.activity.setupViews
import com.xfinity.blueprint.presenter.ComponentEventHandler
import com.xfinity.blueprint.presenter.ScreenPresenter

interface TaggedFragment{
    fun getFragmentTag(): String
}
abstract class ToolbarScreenViewFragment<T: DefaultScreenView> : ScreenViewFragment<T>() {
    override val defaultLayoutId = R.layout.toolbar_screen_view
}

abstract class ScreenViewFragment<T: DefaultScreenView> : androidx.fragment.app.Fragment(), TaggedFragment {
    abstract val architect: DefaultArchitect<T>
    abstract val presenter: ScreenPresenter<T>
    open val toolbarPresenter: ToolbarPresenter? = null

    var toolbarView: ToolbarView? = null
    protected open val defaultLayoutId = R.layout.screen_view

    var layoutId: Int? = null
    var menuId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = setupViews(defaultLayoutId, inflater, container, layoutId)
        onSetupComplete(view)
        architect.initBlueprint(view.findViewById(R.id.container), presenter, (activity as? AppCompatActivity)?.supportActionBar)

        toolbarView = ActionBarToolbarView((activity as? AppCompatActivity)?.supportActionBar)
        toolbarPresenter?.attachToolbarView(toolbarView)
        setHasOptionsMenu(menuId != null)

        return view
    }

    override fun onResume() {
        super.onResume()
        (presenter as? ComponentEventHandler)?.resume()
    }

    override fun onPause() {
        super.onPause()
        (presenter as? ComponentEventHandler)?.pause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home && toolbarView?.onToolbarBackButtonClickedBehavior?.invoke() == true) {
            true
        } else toolbarView?.onActionItemSelectedBehavior?.invoke(item.itemId) ?: false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menuId?.let {
            inflater.inflate(it, menu)
            (toolbarView as? ActionBarToolbarView)?.menu = menu
        } ?: super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * override to do any work that needs to happen after view inflation, but before the architect creates the screen view
     */
    @Suppress("MemberVisibilityCanBePrivate")
    open fun onSetupComplete(view: View) { }
}