package com.xfinity.blueprint_architecture.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xfinity.blueprint.presenter.EventHandlingScreenPresenter
import com.xfinity.blueprint.presenter.ScreenPresenter
import com.xfinity.blueprint_architecture.DefaultScreenView
import com.xfinity.blueprint_architecture.DefaultScreenViewArchitect
import com.xfinity.blueprint_architecture.activity.ScreenViewFragmentDelegate

abstract class ScreenViewFragment : Fragment() {
    abstract var architect: DefaultScreenViewArchitect
    private val screenViewFragmentDelegate = ScreenViewFragmentDelegate()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return screenViewFragmentDelegate.onCreateView(this, inflater, container, architect)
    }

    override fun onResume() {
        super.onResume()
        val presenter = getPresenter()
        if (presenter is EventHandlingScreenPresenter) {
            presenter.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        val presenter = getPresenter()
        if (presenter is EventHandlingScreenPresenter) {
            presenter.pause()
        }
    }

    abstract fun getPresenter(): ScreenPresenter<DefaultScreenView>
}