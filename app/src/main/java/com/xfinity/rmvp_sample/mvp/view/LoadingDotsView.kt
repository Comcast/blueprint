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