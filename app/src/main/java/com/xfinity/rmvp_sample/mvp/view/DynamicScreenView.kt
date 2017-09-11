package com.xfinity.rmvp_sample.mvp.view

import com.xfinity.rmvp.view.EventHandlingScreenView
import com.xfinity.rmvp.view.EventHandlingScreenViewDelegate
import com.xfinity.rmvp_sample.DynamicScreenActivity

class DynamicScreenView(val screenViewDelegate: EventHandlingScreenViewDelegate,
                        val activity: DynamicScreenActivity) :
        EventHandlingScreenView by screenViewDelegate {

    fun setEnabled(enabled: Boolean) {
        activity.setEnabled(enabled)
    }

    fun runOnUiThread(runnable: Runnable) {
        activity.runOnUiThread(runnable)
    }

    fun toast(msg: String) {
        activity.toast(msg)
    }
}