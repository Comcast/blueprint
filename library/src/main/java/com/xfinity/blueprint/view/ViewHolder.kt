package com.xfinity.blueprint.view

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View

abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    inline fun <reified T : View> findView(@IdRes id: Int) = itemView.findViewById(id) as T
}