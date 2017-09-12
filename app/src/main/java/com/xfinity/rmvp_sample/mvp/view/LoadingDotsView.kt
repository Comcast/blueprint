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

package com.xfinity.rmvp_sample.mvp.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.xfinity.rmvp.presenter.ComponentPresenter
import com.xfinity.rmvp.view.ComponentView
import com.xfinity.rmvp.view.ComponentViewBinder
import com.xfinity.rmvp_annotations.ComponentViewClass
import com.xfinity.rmvp_annotations.ComponentViewHolder
import com.xfinity.rmvp_annotations.ComponentViewHolderBinder
import com.xfinity.rmvp_sample.R

@ComponentViewClass(viewHolderClass = LoadingDotsViewHolder::class)
class LoadingDotsView : LoadingDotsViewBase()

@ComponentViewHolder(viewType = R.layout.loading_dots_view)
class LoadingDotsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

@ComponentViewHolderBinder
class LoadingDotsViewBinder : ComponentViewBinder<LoadingDotsViewHolder> {
    override fun bind(componentPresenter: ComponentPresenter, componentView: ComponentView<out LoadingDotsViewHolder>, viewHolder: LoadingDotsViewHolder, position: Int) {
        //if there were any binding, it would go here
    }
}