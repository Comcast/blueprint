package com.xfinity.blueprint_architecture

import android.support.v7.widget.RecyclerView

class RecyclerViewScreenManager(private val recyclerView: RecyclerView) : ScreenManager {
    override fun setBackgroundColor(color: Int) {
        recyclerView.setBackgroundColor(color)
    }

    override fun scrollToBottom() {
        recyclerView.adapter?.let {
            recyclerView.smoothScrollToPosition(it.itemCount - 1)
        }
    }
}

interface ScreenManager {
    fun setBackgroundColor(color: Int)
    fun scrollToBottom()
}