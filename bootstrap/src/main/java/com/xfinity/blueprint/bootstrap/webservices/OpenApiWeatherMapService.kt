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

package com.xfinity.blueprint.bootstrap.webservices

import com.xfinity.blueprint.bootstrap.model.api.CurrentWeather
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenApiWeatherMapService {
    @GET("weather")
    fun currentWeatherByCity(@Query("q") city: String) : Observable<CurrentWeather>

    object Creator {
        @JvmStatic
        fun create(baseUrl: String, client: OkHttpClient, moshi: Moshi): OpenApiWeatherMapService {
            val retrofit = Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()

            return retrofit.create(OpenApiWeatherMapService::class.java)
        }
    }
}


