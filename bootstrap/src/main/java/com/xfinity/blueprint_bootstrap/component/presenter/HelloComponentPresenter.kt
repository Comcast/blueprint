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

package com.xfinity.blueprint_bootstrap.component.presenter

import com.xfinity.blueprint_bootstrap.component.model.SimpleTextModel
import com.xfinity.blueprint_bootstrap.component.view.HelloComponent
import com.xfinity.blueprint.presenter.ComponentPresenter
import com.xfinity.blueprint_annotations.DefaultPresenter

@DefaultPresenter(viewClass = HelloComponent::class)
class HelloComponentPresenter : ComponentPresenter<HelloComponent, SimpleTextModel> {
    override fun present(view: HelloComponent, model: SimpleTextModel) {
        view.setHelloText(model.text)
    }
}