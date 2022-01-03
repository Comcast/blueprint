package com.xfinity.blueprint.architecture.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xfinity.blueprint.architecture.*
import com.xfinity.blueprint.presenter.ComponentEventHandler
import com.xfinity.blueprint.presenter.ScreenPresenter

interface TaggedFragment{
    fun getFragmentTag(): String
}

abstract class ToolbarScreenViewFragment<T: DefaultScreenView> : androidx.fragment.app.Fragment(), TaggedFragment {
    abstract val architect: DefaultArchitect<T>
    abstract val presenter: ScreenPresenter<T>
    abstract val toolbarPresenter: ToolbarPresenter

    var toolbarView: ToolbarView? = null

    var layoutId: Int? = null
    var menuId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(layoutId ?: R.layout.toolbar_screen_view, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)

        val activity = (activity as AppCompatActivity)
        activity.setSupportActionBar(toolbar)

        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        architect.initBlueprint(view.findViewById(R.id.container), presenter, activity.supportActionBar)

        toolbarView = ActionBarToolbarView(activity.supportActionBar)
        toolbarPresenter.attachToolbarView(ActionBarToolbarView(activity.supportActionBar))
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
        return toolbarView?.onActionItemSelectedBehavior?.invoke(item.itemId) ?: false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menuId?.let {
            inflater.inflate(it, menu)
            (toolbarView as? ActionBarToolbarView)?.menu = menu
        }
    }
}