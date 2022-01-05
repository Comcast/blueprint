package com.xfinity.blueprint.architecture.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.xfinity.blueprint.architecture.*
import com.xfinity.blueprint.presenter.ComponentEventHandler
import com.xfinity.blueprint.presenter.ScreenPresenter

abstract class ToolbarScreenViewActivity<T: DefaultScreenView> : ScreenViewActivity<T>() {
    override val defaultLayoutId = R.layout.toolbar_screen_view
}

abstract class ScreenViewActivity<T: DefaultScreenView> : AppCompatActivity() {
    @Suppress("MemberVisibilityCanBePrivate")
    abstract val architect: DefaultArchitect<T>

    @Suppress("MemberVisibilityCanBePrivate")
    abstract val presenter: ScreenPresenter<T>

    @Suppress("MemberVisibilityCanBePrivate")
    open val toolbarPresenter: ToolbarPresenter? = null

    var toolbarView: ActionBarToolbarView? = null

    var layoutId: Int? = null
    var menuId: Int? = null

    protected open val defaultLayoutId = R.layout.screen_view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBlueprintViews(defaultLayoutId, layoutId)
        onSetupComplete()
        architect.initBlueprint(findViewById(android.R.id.content), presenter, supportActionBar)

        //set toolbar presenter after init, so that clients can use the same presenter for both
        toolbarView = ActionBarToolbarView(supportActionBar)
        toolbarPresenter?.attachToolbarView(toolbarView)
    }

    override fun onResume() {
        super.onResume()
        (presenter as? ComponentEventHandler)?.resume()
    }

    override fun onPause() {
        super.onPause()
        (presenter as? ComponentEventHandler)?.pause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return menuId?.let {
            menuInflater.inflate(it, menu)
            toolbarView?.menu = menu
            true
        } ?: super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toolbarView?.onActionItemSelectedBehavior?.invoke(item.itemId) == true) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (toolbarView?.onToolbarBackButtonClickedBehavior?.invoke() == true) {
            true
        } else {
            finish()
            return true
        }
    }

    /**
     * override to do any work that needs to happen after view inflation, but before the architect creates the screen view
     */
    @Suppress("MemberVisibilityCanBePrivate")
    open fun onSetupComplete() { }
}