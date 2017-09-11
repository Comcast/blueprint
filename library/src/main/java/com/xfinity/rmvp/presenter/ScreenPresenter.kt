package com.xfinity.rmvp.presenter

import com.xfinity.rmvp.view.ScreenView

interface ScreenPresenter<in T : ScreenView> {
    fun attachView(screenView: T)
    fun present()
}