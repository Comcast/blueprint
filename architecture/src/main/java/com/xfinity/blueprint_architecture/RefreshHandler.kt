package com.xfinity.blueprint_architecture

import `in`.srain.cube.views.ptr.PtrClassicFrameLayout
import `in`.srain.cube.views.ptr.PtrDefaultHandler
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler
import android.support.design.widget.AppBarLayout
import android.view.View

interface RefreshHandler {
    var isRefreshEnabled: Boolean
    var isRefreshing: Boolean
    fun setOnRefreshBehavior(behavior: () -> Unit)
    fun finishRefresh()
}

class PullToRefreshView(private val ptrFrameLayout: PtrClassicFrameLayout?, appBarLayout: AppBarLayout? = null)
    : RefreshHandler {
    override var isRefreshing: Boolean = false
    private var verticalOffset = 0

    override fun finishRefresh() {
        ptrFrameLayout?.refreshComplete()
        isRefreshing = false
    }

    override var isRefreshEnabled: Boolean = ptrFrameLayout?.isEnabled ?: false
        set(value) {
            ptrFrameLayout?.isEnabled = value
            field = value
        }

    init {
        isRefreshEnabled = true
        appBarLayout?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            this.verticalOffset = verticalOffset
        })
    }

    /**
     * Since My Account app uses it's own loading animation, we disable Ultra PTR library's animations by closing header
     * with attribute ptr_keep_header_when_refresh on xml (e.g. @look screen_view.xml)
     * also by calling .refreshComplete() when refresh started to ensure the baked-in animation stop.
     */
    override fun setOnRefreshBehavior(behavior: () -> Unit) {
        ptrFrameLayout?.setPtrHandler(object : PtrHandler {
            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                isRefreshing = true
                behavior.invoke()
            }

            override fun checkCanDoRefresh(frame: PtrFrameLayout?, content: View?, header: View?): Boolean {
                return verticalOffset == 0 && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header)
            }
        }
        )
    }
}