package com.xfinity.rmvp.view

import android.support.v7.util.DiffUtil
import com.xfinity.rmvp.model.Component
import com.xfinity.rmvp.presenter.ComponentPresenter

/**
 * View (mVp) classs representing a screen
 */
interface ScreenView {
    fun reset()
    fun refresh()
    fun showLoading()
    fun hideLoading()
    fun addComponent(component: Component, notify : Boolean = false, position: Int = -1)
    fun addComponents(components: List<Component>, notify : Boolean = false, position: Int = -1)
    fun removeComponent(position: Int, notify: Boolean = false)
    fun removeComponents(startPosition: Int, endPosition: Int, notify: Boolean = false)
    fun removeComponentsByType(viewType: Int, notify: Boolean = false)
    fun updateComponent(position: Int, component: Component)
    fun onComponentChanged(position: Int)
    fun onComponentRangeChanged(startPosition: Int, endPosition: Int)
    fun onComponentMoved(fromPosition: Int, toPosition: Int)
    fun hasComponent(viewType: Int) : Boolean
    fun positionOfFirst(viewType: Int) : Int
    fun updateComponents(newComponents: List<Component>)
    fun updateComponents(newComponents: List<Component>, diffGenerator: (List<Component>) -> DiffUtil.Callback)
    fun showError(msg: String, actionLabel: String)
}