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

package com.xfinity.blueprint_bootstrap.dagger

import android.app.Application
import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xfinity.blueprint.ComponentRegistry
import com.xfinity.blueprint.architecture.DefaultScreenViewArchitect
import com.xfinity.blueprint_bootstrap.blueprint.AppComponentRegistry
import com.xfinity.blueprint_bootstrap.ApiClient
import com.xfinity.blueprint_bootstrap.MainActivity
import com.xfinity.blueprint_bootstrap.R
import com.xfinity.blueprint_bootstrap.utils.MySchedulers
import com.xfinity.blueprint_bootstrap.utils.Schedulers
import com.xfinity.blueprint_bootstrap.webservices.ApiKeyInterceptor
import com.xfinity.blueprint_bootstrap.webservices.OpenApiWeatherMapService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import okhttp3.OkHttpClient
import javax.inject.Singleton


@Module
abstract class InjectorsModule {
    @PerActivity
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun contributeMainActivityInjector(): MainActivity

    @Binds
    @ApplicationContext
    abstract fun context(application: Application): Context
}

@Module
class ApplicationModule(private val application: Application) {
    @Provides
    @Singleton
    @Default
    fun provideOkHttpClient() : OkHttpClient = OkHttpClient()

    @Provides
    @Singleton
    @Authenticating
    fun provideOWMOkHttpClient(@Default client: OkHttpClient): OkHttpClient = client
            .newBuilder()
            .addInterceptor(ApiKeyInterceptor(application.resources.getString(R.string.open_weather_map_api_key)))
                .build()

    @Provides
    @Singleton
    fun provideMoshi() : Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideOpenWeatherMapService(@Authenticating okHttpClient: OkHttpClient, moshi: Moshi) : OpenApiWeatherMapService =
            OpenApiWeatherMapService.Creator.create(application.resources.getString(R.string.open_weather_map_api_url),
                okHttpClient, moshi)

    @Provides
    @Singleton
    fun provideApiClient(openApiWeatherMapService: OpenApiWeatherMapService) : ApiClient = ApiClient(openApiWeatherMapService)

    @Provides
    @Singleton
    fun getSchedulers() : Schedulers = MySchedulers()

    @Provides
    @Singleton
    fun getComponentRegistry() : ComponentRegistry = AppComponentRegistry()

    @Provides
    @Singleton
    fun getArchitect(componentRegistry: ComponentRegistry) : DefaultScreenViewArchitect =
            DefaultScreenViewArchitect(componentRegistry)
}