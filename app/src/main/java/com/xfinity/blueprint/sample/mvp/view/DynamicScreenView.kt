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

package com.xfinity.blueprint.sample.mvp.view

import com.xfinity.blueprint.view.ScreenView
import com.xfinity.blueprint.view.ScreenViewDelegate

interface DynamicScreenView {
    fun setEnabled(enabled: Boolean)
    fun runOnUiThread(runnable: Runnable)
    fun toast(msg: String)
}

class DefaultDynamicScreenView(private val screenViewDelegate: ScreenViewDelegate,
                               private val delegate: DynamicScreenView)
    : ScreenView by screenViewDelegate, DynamicScreenView {

    override fun setEnabled(enabled: Boolean) {
        delegate.setEnabled(enabled)
    }

    override fun runOnUiThread(runnable: Runnable) {
        delegate.runOnUiThread(runnable)
    }

    override fun toast(msg: String) {
        delegate.toast(msg)
    }
}