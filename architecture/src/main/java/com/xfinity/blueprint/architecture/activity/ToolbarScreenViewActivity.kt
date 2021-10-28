package com.xfinity.blueprint.architecture.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.xfinity.blueprint.architecture.ToolbarScreenPresenter
import com.xfinity.blueprint.architecture.ToolbarScreenViewArchitect

abstract class ToolbarScreenViewActivity : AppCompatActivity() {
    @Suppress("MemberVisibilityCanBePrivate")
    abstract var architect: ToolbarScreenViewArchitect
    abstract val presenter: ToolbarScreenPresenter

    var menuId: Int? = null
    private val screenViewActivityDelegate = ScreenViewActivityDelegate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenViewActivityDelegate.onCreate(this, architect)
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return menuId?.let {
            menuInflater.inflate(it, menu)
            architect.screenView.menu = menu
            true
        } ?: super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return architect.screenView.onActionItemSelectedBehavior.invoke(item.itemId)
    }
}