package com.xfinity.rmvp.view

import android.support.v7.widget.RecyclerView
import com.xfinity.rmvp.presenter.ComponentPresenter

interface ComponentViewBinder<in T : RecyclerView.ViewHolder> {
    fun bind(componentPresenter: ComponentPresenter, componentView: ComponentView<out T>, viewHolder: T, position: Int)
}

class ClickableComponentViewBinder : ComponentViewBinder<RecyclerView.ViewHolder> {
    override fun bind(componentPresenter: ComponentPresenter,
                      componentView: ComponentView<out RecyclerView.ViewHolder>,
                      viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { componentPresenter.onComponentClicked(componentView, position) }
    }
}