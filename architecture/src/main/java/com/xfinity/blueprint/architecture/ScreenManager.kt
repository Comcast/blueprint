package com.xfinity.blueprint.architecture

import androidx.recyclerview.widget.RecyclerView

class RecyclerViewScreenManager(private val recyclerView: androidx.recyclerview.widget.RecyclerView) : ScreenManager {
    override fun setBackgroundColor(color: Int) {
        recyclerView.setBackgroundColor(color)
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
    fun scrollToBottom()
    fun scrollToTop()
}