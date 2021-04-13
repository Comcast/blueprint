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

package com.xfinity.blueprint_sample.mvp.model

open class StaticScreenModel {
    val headerModel: HeaderModel = HeaderModel()
    val footerModel: FooterModel = FooterModel()
    val dataItemModels: List<DataItemModel> = listOf(DataItemModel(), DataItemModel(), DataItemModel(),
            DataItemModel(), DataItemModel(), DataItemModel())

    init {
        headerModel.enabled = true
        footerModel.enabled = true
        dataItemModels.forEach({
            it.enabled = true
        })
    }
}