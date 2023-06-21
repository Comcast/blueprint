package com.xfinity.blueprint_sample.mvp.view

import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xfinity.blueprint_annotations.ComponentViewClass
import com.xfinity.blueprint_annotations.ComponentViewHolder
import com.xfinity.blueprint_sample.R


@ComponentViewHolder(viewType = "clickable_random_view")
class ClickableRandomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var xfinity_app: ImageView = itemView.findViewById<ImageView>(R.id.imageView2)
    var xfintiy_text: TextView = itemView.findViewById<TextView>(R.id.xfinity_text)

}

@ComponentViewClass(viewHolderClass = ClickableRandomViewHolder::class)
class ClickableRandomView: ClickableRandomViewBase(){

}