package com.xfinity.blueprint_sample.mvp.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xfinity.blueprint_annotations.ComponentViewClass
import com.xfinity.blueprint_annotations.ComponentViewHolder
import com.xfinity.blueprint_sample.R

@ComponentViewHolder(viewType = "random_layout")
class RandomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val randomtext: TextView = itemView.findViewById<TextView>(R.id.randomTextView)
}

@ComponentViewClass(viewHolderClass = RandomViewHolder::class)
class RandomView: RandomViewBase(){
    fun setEnabled(enabled: Boolean) {
        viewHolder.itemView.isEnabled = enabled

    }


}