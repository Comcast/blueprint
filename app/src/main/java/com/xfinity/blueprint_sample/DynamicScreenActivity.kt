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

package com.xfinity.blueprint_sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint.view.ScreenViewDelegate
import com.xfinity.blueprint_sample.blueprint.AppComponentRegistry
import com.xfinity.blueprint_sample.mvp.presenter.DynamicScreenPresenter
import com.xfinity.blueprint_sample.mvp.view.DefaultDynamicScreenView
import com.xfinity.blueprint_sample.mvp.view.DynamicScreenView

/**
 * The dynamic screen example documents how to compose a screen that can change based on the user's interactions with
 * the Components.  The dynamic screen handles Component events, and can respond to them by adding or removing Components
 */
class DynamicScreenActivity : AppCompatActivity(), DynamicScreenView {
    //These would be injected
    private val componentEventManager = ComponentEventManager()
    private val componentRegistry = AppComponentRegistry(componentEventManager, defaultItemId, defaultItemName)
    private val presenter = DynamicScreenPresenter(componentEventManager)

    private lateinit var screenViewDelegate: ScreenViewDelegate
    private lateinit var mainScreenView: DefaultDynamicScreenView

    lateinit var content : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        content = findViewById(R.id.content)
        val recyclerView = findViewById(R.id.recycler_view) as androidx.recyclerview.widget.RecyclerView
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)

        screenViewDelegate = ScreenViewDelegate(componentRegistry, null, recyclerView)
        mainScreenView = DefaultDynamicScreenView(screenViewDelegate, this)
        presenter.attachView(mainScreenView)
        presenter.present()

        recyclerView.adapter = screenViewDelegate.componentAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.remove -> {
                presenter.removeItemRequested()
                true
            }
            R.id.refresh_data_items -> {
                presenter.refreshDataItems()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
    }

    override fun setEnabled(enabled: Boolean) {
        content.setBackgroundColor(resources.getColor(android.R.color.white))
    }

    override fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    companion object {
        const val defaultItemName = "DefaultDataItemName"
        const val defaultItemId = 0
    }
}
