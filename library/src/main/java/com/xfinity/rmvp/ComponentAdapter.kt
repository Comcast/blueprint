/*
 * Copyright 2017 Comcast Cable Communications Management, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xfinity.rmvp

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.xfinity.rmvp.model.Component
import com.xfinity.rmvp.presenter.ComponentPresenter
import com.xfinity.rmvp.view.ComponentView

open class ComponentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private val componentRegistry: ComponentRegistry

    private val presenterMap = mutableMapOf<Int, ComponentPresenter>()
    internal val components = mutableListOf<Component>()
    private val componentViews = mutableListOf<ComponentView<RecyclerView.ViewHolder>>()

    constructor(componentRegistry: ComponentRegistry) {
        this.componentRegistry = componentRegistry
    }

    @Suppress("unused")
    constructor(componentRegistry: ComponentRegistry,
                components: List<Component>) {
        this.componentRegistry = componentRegistry
        this.components.addAll(components)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val componentView = componentRegistry.getComponentView(viewType) ?:
                throw IllegalStateException("No ComponentView registered for type " + viewType)

        return componentView.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val componentView = componentViews[position]
        val presenter: ComponentPresenter = presenterMap[componentView.getViewType()] ?:
                throw IllegalStateException("Presenter for " + componentView.javaClass.simpleName + " is null.")

        val componentModel = components[position].model

        componentView.onBindViewHolder(presenter, holder, position)
        presenter.present(componentView, componentModel)
    }

    override fun getItemCount(): Int {
        return components.size
    }

    override fun getItemViewType(position: Int): Int {
        return components[position].viewType
    }

    fun addComponent(component: Component, notify: Boolean = false, position: Int = -1) {

        //By default, add views at the end of the list
        val adjustedPosition = if (position == -1) {
            components.size
        } else {
            position
        }

        addComponentData(adjustedPosition, component)
        if (notify) {
            notifyItemInserted(adjustedPosition)
        }
    }

    fun addComponents(componentsToAdd: List<Component>, notify: Boolean = false, position: Int = -1) {

        //By default, add views at the end of the list
        val startPosition = if (position == -1) {
            components.size
        } else {
            position
        }

        var count = startPosition
        for (component in componentsToAdd) {
            addComponentData(count, component)
            count++
        }

        if (notify) {
            notifyItemRangeInserted(startPosition, count-1)
        }
    }

    fun removeComponent(position: Int, notify: Boolean) {
        removeComponentData(position)
        if (notify) {
            notifyItemRemoved(position)
        }
    }

    fun removeComponents(startPosition: Int, endPosition: Int, notify: Boolean) {
        for (i in endPosition..startPosition) {
            removeComponentData(i)
        }

        if (notify) {
            notifyItemRangeRemoved(startPosition, endPosition)
        }
    }

    fun removeComponentsByType(viewType: Int, notify: Boolean) {
        val iterate = components.listIterator()
        while (iterate.hasNext()) {
            if (iterate.next().viewType == viewType) {
                iterate.remove()
            }
        }

        val iterator = componentViews.listIterator()
        while (iterator.hasNext()) {
            val componentView = iterator.next()
            if (componentView.getViewType() == viewType) {
                val index = componentViews.indexOf(componentView)
                iterator.remove()
                if (notify) {
                    notifyItemRemoved(index)
                }
            }
        }
    }

    fun hasComponent(viewType: Int) : Boolean {
        components.forEach {
            if (it.viewType == viewType) {
                return true
            }
        }

        return false
    }

    fun getPositionsForViewType(viewType: Int) : List<Int> {
        val positions = mutableListOf<Int>()
        components.forEachIndexed { index, component ->
            if (component.viewType == viewType) {
                positions.add(index)
            }
        }

        return positions
    }

    fun positionOfFirst(viewType: Int) : Int {
        components.forEachIndexed { index, component ->
            if (component.viewType == viewType) {
                return index
            }
        }

        return -1
    }


    fun clear(notify: Boolean) {
        val count = itemCount
        
        components.clear()
        componentViews.clear()
        if (notify) {
            notifyItemRangeRemoved(0, count)
        }
    }

    fun addComponentData(position: Int, component: Component) {
        components.add(position, component)

        val componentView = componentRegistry.getComponentView(component.viewType) ?:
                throw IllegalStateException("No ComponentView registered for type " + component.viewType)

        if (!presenterMap.containsValue(component.presenter)) {
            if (component.presenter != null) {
                presenterMap.put(component.viewType, component.presenter)
            } else {
                val defaultPresenter = componentRegistry.getDefaultPresenter(component.viewType)
                if (defaultPresenter == null) {
                    throw IllegalStateException("Presenter for " + componentView.javaClass.simpleName +
                            " is not specified in presenterMap or Component Registry")
                } else {
                    presenterMap.put(component.viewType, defaultPresenter)
                }
            }
        }

        componentViews.add(position, componentView)
    }

    fun removeComponentData(position: Int) {
        components.removeAt(position)

        if (componentViews.size > position) {
            componentViews.removeAt(position)
        }
    }
}
