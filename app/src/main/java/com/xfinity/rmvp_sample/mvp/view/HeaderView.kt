package com.xfinity.rmvp_sample.mvp.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.xfinity.rmvp_annotations.ComponentViewClass
import com.xfinity.rmvp_annotations.ComponentViewHolder
import com.xfinity.rmvp_sample.R

@ComponentViewClass(viewHolderClass = HeaderViewHolder::class)
class HeaderView : HeaderViewBase() {

    fun setHeader(header: String) {
        viewHolder.textView.text = header
    }

    fun setEnabled(enabled: Boolean) {
        viewHolder.itemView.isEnabled = enabled
    }
}

@ComponentViewHolder(viewType = R.layout.header_view)
class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView : TextView = itemView.findViewById(R.id.header) as TextView
}
