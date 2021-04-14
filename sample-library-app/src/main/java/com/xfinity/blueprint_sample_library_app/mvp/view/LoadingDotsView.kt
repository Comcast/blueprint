/*
 * Copyright 2017 Comcast Cable Communications Management, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xfinity.blueprint_sample_library_app.mvp.view

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.xfinity.blueprint.model.ComponentModel
import com.xfinity.blueprint.presenter.ComponentPresenter
import com.xfinity.blueprint.view.ComponentView
import com.xfinity.blueprint.view.ComponentViewBinder
import com.xfinity.blueprint_annotations.ComponentViewClass
import com.xfinity.blueprint_annotations.ComponentViewHolder
import com.xfinity.blueprint_annotations.ComponentViewHolderBinder
import com.xfinity.blueprint_sample.R

@ComponentViewClass(viewHolderClass = LoadingDotsViewHolder::class)
class LoadingDotsView : LoadingDotsViewBase()

@ComponentViewHolder(viewType = "loading_dots_view")
class LoadingDotsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

@ComponentViewHolderBinder
class LoadingDotsViewBinder : ComponentViewBinder<LoadingDotsViewHolder> {

    override fun bind(componentPresenter: ComponentPresenter<ComponentView<*>, ComponentModel>,
                      componentView: ComponentView<out LoadingDotsViewHolder>,
                      viewHolder: LoadingDotsViewHolder, position: Int) {
        //if there were any binding, it would go here
    }
}