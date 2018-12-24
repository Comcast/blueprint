package com.xfinity.blueprint.bootstrap.component.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.xfinity.blueprint_annotations.ComponentViewClass
import com.xfinity.blueprint_annotations.ComponentViewHolder
import com.xfinity.bootstrap.R

@ComponentViewClass(viewHolderClass = HelloComponentViewHolder::class)
class HelloComponent : HelloComponentBase()

@ComponentViewHolder(viewType = R.layout.hello_component)
class HelloComponentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val hello : TextView = itemView.findViewById(R.id.hello) as TextView
}