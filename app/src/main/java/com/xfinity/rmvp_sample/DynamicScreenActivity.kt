package com.xfinity.rmvp_sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.xfinity.rmvp.event.ComponentEventManager
import com.xfinity.rmvp.view.EventHandlingScreenViewDelegate
import com.xfinity.rmvp_sample.mvp.presenter.DynamicScreenPresenter
import com.xfinity.rmvp_sample.mvp.view.DynamicScreenView
import com.xfinity.rmvp_sample.rmvp.AppComponentRegistry
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

/**
 * The dynamic screen example documents how to compose a screen that can change based on the user's interactions with
 * the Components.  The dynamic screen handles Component events, and can respond to them by adding or removing Components
 */
class DynamicScreenActivity : AppCompatActivity() {
    //These would be injected
    private val componentEventManager = ComponentEventManager()
    private val componentRegistry = AppComponentRegistry(componentEventManager, defaultItemId, defaultItemName)
    private val presenter = DynamicScreenPresenter()

    private val screenViewDelegate: EventHandlingScreenViewDelegate =
            EventHandlingScreenViewDelegate(componentRegistry, componentEventManager, presenter)
    private val mainScreenView = DynamicScreenView(screenViewDelegate, this)

    lateinit var content : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        content = findViewById(R.id.content)
        val recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        presenter.attachView(mainScreenView)
        presenter.present()

        recyclerView.itemAnimator = SlideInUpAnimator()
        recyclerView.adapter = screenViewDelegate.getComponentAdapter()
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
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    fun setEnabled(enabled: Boolean) {
        content.setBackgroundColor(resources.getColor(android.R.color.white))
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    companion object {
        const val defaultItemName = "DefaultDataItemName"
        const val defaultItemId = 0
    }
}
