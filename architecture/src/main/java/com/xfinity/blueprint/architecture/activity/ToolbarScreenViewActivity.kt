package com.xfinity.blueprint.architecture.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.xfinity.blueprint.presenter.EventHandlingScreenPresenter
import com.xfinity.blueprint.presenter.ScreenPresenter
import com.xfinity.blueprint.architecture.ToolbarScreenView
import com.xfinity.blueprint.architecture.ToolbarScreenViewArchitect

abstract class ToolbarScreenViewActivity : AppCompatActivity() {
    @Suppress("MemberVisibilityCanBePrivate")
    abstract var architect: ToolbarScreenViewArchitect
    private val screenViewActivityDelegate = ScreenViewActivityDelegate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenViewActivityDelegate.onCreate(this, architect)
    }

    override fun onResume() {
        super.onResume()
        if (presenter is EventHandlingScreenPresenter) {
            (presenter as EventHandlingScreenPresenter).resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (presenter is EventHandlingScreenPresenter) {
            (presenter as EventHandlingScreenPresenter).pause()
        }
    }

    abstract val presenter: ScreenPresenter<ToolbarScreenView>
}