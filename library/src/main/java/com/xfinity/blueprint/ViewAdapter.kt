package com.xfinity.blueprint

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.xfinity.blueprint.model.Component

class ViewAdapter(private val recyclerView: RecyclerView,
                  private var loadingView: View? = null) : ViewRegistry {

    override fun updateComponents(newComponents: List<Component>, behavior: () -> Unit) {
        if (!recyclerView.isComputingLayout) {
            behavior.invoke()
            return
        }
        recyclerView.addOnScrollListener(RecyclerViewScrollHandler(behavior))
    }

    override fun showLoading() {
        loadingView?.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loadingView?.visibility = View.GONE
    }

    private inner class RecyclerViewScrollHandler(private val behavior: () -> Unit) : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == SCROLL_STATE_IDLE && !recyclerView.isComputingLayout) {
                behavior.invoke()
            }
        }
    }
}

interface ViewRegistry {
    fun updateComponents(newComponents: List<Component>, behavior: () -> Unit)
    fun showLoading()
    fun hideLoading()
}