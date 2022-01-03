package com.xfinity.blueprint.architecture.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xfinity.blueprint.architecture.DefaultScreenViewArchitect
import com.xfinity.blueprint.architecture.R
import com.xfinity.blueprint.architecture.fragment.ScreenViewFragment

class ScreenViewActivityDelegate {
    fun onCreate(activity: ScreenViewActivityLegacy, architect: DefaultScreenViewArchitect)  {
        setupViews(activity)
        architect.initBlueprint(activity.findViewById(android.R.id.content), activity.presenter)
    }

//    fun onCreate(activity: ToolbarScreenViewActivity, architect: ToolbarScreenViewArchitect, layoutId: Int? = null)  {
//        setupViews(activity, layoutId)
//        architect.initBlueprint(activity.findViewById(android.R.id.content), activity.presenter, activity.supportActionBar)
//    }

    private fun setupViews(activity: AppCompatActivity, layoutId: Int? = null) {
        activity.setContentView(layoutId ?: R.layout.toolbar_screen_view)
        val toolbar: Toolbar = activity.findViewById(R.id.toolbar)
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView = activity.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
    }
}

fun AppCompatActivity.setupBlueprintViews(defaultLayoutId: Int, layoutId: Int? = null) {
    setContentView(layoutId ?: defaultLayoutId)
    setSupportActionBar(findViewById(R.id.toolbar))

    val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
    recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this,
        androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
}

class ScreenViewFragmentDelegate {
    fun onCreateView(fragment: ScreenViewFragment, inflater: LayoutInflater, container: ViewGroup?, architect: DefaultScreenViewArchitect, layoutId: Int? = null) : View {
        val view = inflater.inflate(layoutId ?: R.layout.screen_view, container, false)
        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(fragment.context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)

        architect.initBlueprint(view.findViewById(R.id.container), fragment.presenter)
        return view
    }
}