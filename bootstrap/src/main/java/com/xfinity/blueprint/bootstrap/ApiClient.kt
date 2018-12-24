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

package com.xfinity.blueprint.bootstrap

import com.xfinity.blueprint.bootstrap.model.api.CurrentWeather
import com.xfinity.blueprint.bootstrap.webservices.OpenApiWeatherMapService
import io.reactivex.Observable

class ApiClient(private val openApiWeatherMapService: OpenApiWeatherMapService) {
    fun getCurrentWeatherByCity(city: String) : Observable<CurrentWeather> =
            openApiWeatherMapService.currentWeatherByCity(city)
}