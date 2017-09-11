package com.xfinity.rmvp.view

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.xfinity.rmvp.presenter.ComponentPresenter

/**
 * View (mVp) class representing an component in a recyclerview adapter
 */
interface ComponentView<T : RecyclerView.ViewHolder> {
    var viewHolder: T
    val componentViewBinder: ComponentViewBinder<T>
    fun onCreateViewHolder(parent: ViewGroup) : T
    fun onBindViewHolder(componentPresenter: ComponentPresenter, viewHolder: RecyclerView.ViewHolder, position: Int)
    fun getViewType() : Int
}