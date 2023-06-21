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

package com.xfinity.blueprint_sample_library.mvp.model

import com.xfinity.blueprint.model.ComponentModel

class DataItemModel : ComponentModel {
    val data : String = "Pew Pew Pew"
    var enabled = false

    override fun equals(other: Any?): Boolean {
        return other is DataItemModel && other.enabled == enabled
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + enabled.hashCode()
        return result
    }
}