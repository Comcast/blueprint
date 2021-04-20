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

package com.xfinity.blueprint

import androidx.recyclerview.widget.RecyclerView
import com.xfinity.blueprint.model.ComponentModel
import com.xfinity.blueprint.presenter.ComponentPresenter
import com.xfinity.blueprint.view.ComponentView

interface ComponentRegistry {
    fun getComponentView(viewType: Int): ComponentView<androidx.recyclerview.widget.RecyclerView.ViewHolder>?
    fun getDefaultPresenter(viewType: Int, vararg args: Any): ComponentPresenter<ComponentView<*>, ComponentModel>?
    fun getDefaultPresenter(componentView: ComponentView<androidx.recyclerview.widget.RecyclerView.ViewHolder>, vararg args: Any): ComponentPresenter<ComponentView<*>, ComponentModel>?
}

open class CompositeComponentRegistry(val componentRegistries: List<ComponentRegistry> = listOf()) : ComponentRegistry {
    override fun getComponentView(viewType: Int): ComponentView<RecyclerView.ViewHolder>? {
        componentRegistries.forEach {
            val componentView = it.getComponentView(viewType)
            if (componentView != null) {
                return componentView
            }
        }

        return null
    }

    override fun getDefaultPresenter(viewType: Int, vararg args: Any): ComponentPresenter<ComponentView<*>, ComponentModel>? {
        componentRegistries.forEach {
            val componentPresenter = it.getDefaultPresenter(viewType, args)
            if (componentPresenter != null) {
                return componentPresenter
            }
        }

        return null
    }

    override fun getDefaultPresenter(componentView: ComponentView<RecyclerView.ViewHolder>, vararg args: Any): ComponentPresenter<ComponentView<*>, ComponentModel>? {
        componentRegistries.forEach {
            val componentPresenter = it.getDefaultPresenter(componentView, args)
            if (componentPresenter != null) {
                return componentPresenter
            }
        }

        return null
    }

}