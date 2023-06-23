package com.xfinity.blueprint_sample.mvp.view

import android.view.View
import com.xfinity.blueprint.architecture.component.BasicComponentView
import com.xfinity.blueprint.architecture.component.BasicComponentViewHolder
import com.xfinity.blueprint_annotations.ComponentViewClass
import com.xfinity.blueprint_annotations.ComponentViewHolder

/**
 * An example of a Basic Component and ViewHolder.  The ViewHolder can contain any subset of the
 * views supported by the BasicComponentView, including three text fields, three buttons or image
 * buttons, and one image. Since the functions for presenting these views is already part of the
 * BasicComponentView class, no code is needed here.  These declarations simply name this new
 * component, and associate it with its XML layout file.
 */
@ComponentViewClass(viewHolderClass = BasicComponentExampleViewHolder::class)
class BasicComponentExample : BasicComponentExampleBase(),
    BasicComponentView<BasicComponentExampleViewHolder> {
    //BasicComponentView boilerplate.
    override val basicComponentViewHolder: BasicComponentExampleViewHolder by lazy {
        viewHolder
    } //BasicComponentView boilerplate
}

@ComponentViewHolder(viewType = "basic_component_example")
class BasicComponentExampleViewHolder(itemView: View) : BasicComponentViewHolder(itemView)