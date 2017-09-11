package com.xfinity.rmvp_sample.mvp.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.xfinity.rmvp_annotations.ComponentViewClass
import com.xfinity.rmvp_annotations.ComponentViewHolder
import com.xfinity.rmvp_annotations.ClickableComponentBinder
import com.xfinity.rmvp_sample.R

@ClickableComponentBinder
@ComponentViewClass(viewHolderClass = DataItemViewHolder::class)
class DataItemView : DataItemViewBase() {
    fun setData(data: String) {
        viewHolder.textView.text = data
    }
}

@ComponentViewHolder(viewType = R.layout.data_item_view)
class DataItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView : TextView = itemView.findViewById(R.id.data) as TextView
}
