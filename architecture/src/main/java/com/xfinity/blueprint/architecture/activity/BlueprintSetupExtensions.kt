package com.xfinity.blueprint.architecture.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.xfinity.blueprint.architecture.R

fun AppCompatActivity.setupViews(defaultLayoutId: Int, layoutId: Int? = null) {
    setContentView(layoutId ?: defaultLayoutId)
    val toolbar: Toolbar? = findViewById(R.id.toolbar)
    toolbar?.let { setSupportActionBar(it) }

    val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
    recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this,
        androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
}

fun Fragment.setupViews(defaultLayoutId: Int, inflater: LayoutInflater, container: ViewGroup?, layoutId: Int? = null) : View {
    val view = inflater.inflate(layoutId ?: defaultLayoutId, container, false)
    val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
    val toolbar: Toolbar? = view.findViewById(R.id.toolbar)
    toolbar?.let { (activity as AppCompatActivity).setSupportActionBar(it) }

    recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
    return view
}