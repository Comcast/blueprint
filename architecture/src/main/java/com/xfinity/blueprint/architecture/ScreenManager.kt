package com.xfinity.blueprint.architecture

import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.Drawable

class RecyclerViewScreenManager(private val recyclerView: RecyclerView) : ScreenManager {
    override fun setBackgroundColor(color: Int) {
        recyclerView.setBackgroundColor(color)
    }

    override fun setBackgroundImage(drawable: Drawable) {
        recyclerView.background = drawable
    }
    
    override fun scrollToBottom() {
        recyclerView.adapter?.let {
            recyclerView.smoothScrollToPosition(it.itemCount - 1)
        }
    }

    override fun scrollToTop() {
        recyclerView.smoothScrollToPosition(0)
    }
}

interface ScreenManager {
    fun setBackgroundColor(color: Int)
    fun setBackgroundImage(drawable: Drawable)
    fun scrollToBottom()
    fun scrollToTop()
}
