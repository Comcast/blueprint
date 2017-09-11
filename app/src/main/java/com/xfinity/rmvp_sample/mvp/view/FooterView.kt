package com.xfinity.rmvp_sample.mvp.view

import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.xfinity.rmvp_annotations.ComponentViewClass
import com.xfinity.rmvp_annotations.ComponentViewHolder
import com.xfinity.rmvp_sample.R

@ComponentViewClass(viewHolderClass = FooterViewHolder::class)
class FooterView : FooterViewBase() {
    fun setFooter(footer: String) {
        viewHolder.textView.text = footer
    }
}

@ComponentViewHolder(viewType = R.layout.footer_view)
class FooterViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
    val textView : TextView = itemView.findViewById(R.id.footer) as TextView
}

