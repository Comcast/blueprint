/*
 *
 *  * Copyright 2018 Comcast Cable Communications Management, LLC
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.xfinity.blueprint.bootstrap.component.view

import androidx.recyclerview.widget.RecyclerView
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