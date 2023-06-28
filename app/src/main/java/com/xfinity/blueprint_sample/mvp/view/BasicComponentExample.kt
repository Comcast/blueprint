package com.xfinity.blueprint_sample.mvp.view

import android.view.View
import com.xfinity.blueprint.architecture.component.BasicComponentViewHolder
import com.xfinity.blueprint_annotations.BasicComponent
import com.xfinity.blueprint_annotations.ComponentViewHolder

/**
 * An example of a Basic Component definition.  The layout can make use of any subset of the
 * views supported by the BasicComponentView, including three text fields, three buttons or image
 * buttons, and one image. Since the functions for presenting these views is already part of the
 * BasicComponentView class, no code is needed here.  This declaration simply names the new
 * component, and associate it with its XML layout file.  The ComponentView and ComponentPresenter
 * classes are generated for you.
 *
 * Naming is important: If you call your view holder SomethingViewHolder,
 * you will get a Something (ComponentView) class and a SomethingPresenter (ComponentPresenter)
 * class.  If you call it Something, (no ViewHolder on the end) you will get a
 * SomethingView class and an SomethingPresenter class
 */

@BasicComponent
@ComponentViewHolder(viewType = "basic_component_example")
class BasicComponentExampleViewHolder(itemView: View) : BasicComponentViewHolder(itemView)