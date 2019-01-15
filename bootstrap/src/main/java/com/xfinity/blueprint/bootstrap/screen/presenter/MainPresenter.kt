/*
 *
 *  * Copyright 2018 Comcast Cable Communications Management, LLC
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.xfinity.blueprint.bootstrap.screen.presenter

import com.xfinity.blueprint.architecture.DefaultScreenView
import com.xfinity.blueprint.blueprint.AppComponentRegistry
import com.xfinity.blueprint.bootstrap.component.model.SimpleTextModel
import com.xfinity.blueprint.bootstrap.screen.model.MainScreenModel
import com.xfinity.blueprint.bootstrap.model.api.CurrentWeather
import com.xfinity.blueprint.bootstrap.utils.Schedulers
import com.xfinity.blueprint.model.Component
import com.xfinity.blueprint.presenter.ScreenPresenter
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class MainPresenter @Inject constructor(private val model: MainScreenModel,
                                        private val schedulers: Schedulers) : ScreenPresenter<DefaultScreenView> {
    private lateinit var screenView: DefaultScreenView
    override fun attachView(screenView: DefaultScreenView) {
        this.screenView = screenView
    }

    private var disposable: Disposable? = null
    private var modelData: CurrentWeather? = null

    fun onResume() {
        model.loadData("philadelphia").subscribeOn(schedulers.ioThread)
                .observeOn(schedulers.mainThread)
                .subscribe(object: Observer<CurrentWeather> {
                    override fun onComplete() { }

                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(t: CurrentWeather) {
                        modelData = t
                        present()
                    }

                    override fun onError(e: Throwable) {
                        Timber.e("Something went wrong: ${e.message}")
                    }

                })
    }

    fun onPause() {
        disposable?.dispose()
    }

    override fun present() {
        val components = mutableListOf<Component>()
        modelData?.weather?.firstOrNull()?.let {
            components.add(Component(SimpleTextModel(it.main), AppComponentRegistry.HelloComponent_VIEW_TYPE))
        }

        screenView.updateComponents(components)
    }
}