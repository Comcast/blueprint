/*
 *
 *  * Copyright 2018 Mark Dappollone
 *
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

package com.xfinity.blueprint.bootstrap.screen.model

import com.xfinity.blueprint.bootstrap.ApiClient
import com.xfinity.blueprint.bootstrap.model.api.CurrentWeather
import io.reactivex.Observable
import javax.inject.Inject

class MainScreenModel @Inject constructor(private val apiClient: ApiClient) {
    fun loadData(city: String) : Observable<CurrentWeather> = apiClient.getCurrentWeatherByCity(city)
}