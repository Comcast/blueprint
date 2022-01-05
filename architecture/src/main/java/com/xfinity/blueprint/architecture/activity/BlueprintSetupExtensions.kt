package com.xfinity.blueprint.architecture.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.xfinity.blueprint.architecture.R

fun AppCompatActivity.setupBlueprintViews(defaultLayoutId: Int, layoutId: Int? = null) {
    setContentView(layoutId ?: defaultLayoutId)
    setSupportActionBar(findViewById(R.id.toolbar))

    val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
    recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this,
        androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
}

fun Fragment.setupBlueprintViews(defaultLayoutId: Int, inflater: LayoutInflater, container: ViewGroup?, layoutId: Int? = null) : View {
    val view = inflater.inflate(layoutId ?: defaultLayoutId, container, false)
    val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
    (activity as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolbar))

    recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
    return view
}