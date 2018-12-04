package com.xfinity.blueprint.architecture.activity

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xfinity.blueprint.architecture.DefaultScreenViewArchitect
import com.xfinity.blueprint.architecture.R
import com.xfinity.blueprint.architecture.ToolbarScreenViewArchitect
import com.xfinity.blueprint.architecture.fragment.ScreenViewFragment

class ScreenViewActivityDelegate {
    fun onCreate(activity: ScreenViewActivity, architect: DefaultScreenViewArchitect)  {
        setupViews(activity)
        architect.initBlueprint(activity.findViewById(android.R.id.content), activity.presenter)
    }

    fun onCreate(activity: ToolbarScreenViewActivity, architect: ToolbarScreenViewArchitect)  {
        setupViews(activity)
        architect.initBlueprint(activity.findViewById(android.R.id.content), activity.presenter, activity.supportActionBar)
    }

    fun setupViews(activity: AppCompatActivity) {
        activity.setContentView(R.layout.screen_view_activity)
        val toolbar: Toolbar = activity.findViewById(R.id.toolbar)
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView = activity.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }
}

class ScreenViewFragmentDelegate {
    fun onCreateView(fragment: ScreenViewFragment, inflater: LayoutInflater, container: ViewGroup?, architect: DefaultScreenViewArchitect) : View {
        val view = inflater.inflate(R.layout.screen_view_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(fragment.context, LinearLayoutManager.VERTICAL, false)

        architect.initBlueprint(view.findViewById(R.id.container), fragment.presenter)
        return view
    }
}
