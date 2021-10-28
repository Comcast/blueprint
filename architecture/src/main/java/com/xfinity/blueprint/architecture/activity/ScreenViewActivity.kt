package com.xfinity.blueprint.architecture.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xfinity.blueprint.architecture.DefaultScreenView
import com.xfinity.blueprint.architecture.DefaultScreenViewArchitect
import com.xfinity.blueprint.presenter.ScreenPresenter

abstract class ScreenViewActivity : AppCompatActivity() {
    @Suppress("MemberVisibilityCanBePrivate")
    abstract var architect: DefaultScreenViewArchitect
    abstract val presenter: ScreenPresenter<DefaultScreenView>

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
}
