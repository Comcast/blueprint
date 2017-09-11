package com.xfinity.rmvp

import android.support.v7.widget.RecyclerView
import com.xfinity.rmvp.presenter.ComponentPresenter
import com.xfinity.rmvp.view.ComponentView

interface ComponentRegistry {
    fun getComponentView(viewType: Int) : ComponentView<RecyclerView.ViewHolder>?
    fun getDefaultPresenter(viewType: Int, vararg args : Any) : ComponentPresenter?
    fun getDefaultPresenter(componentView: ComponentView<RecyclerView.ViewHolder>, vararg args : Any): ComponentPresenter?
}
