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

package com.xfinity.rmvp_sample

import com.nhaarman.mockito_kotlin.*
import com.xfinity.rmvp.event.ComponentEventManager
import com.xfinity.rmvp.model.Component
import com.xfinity.rmvp.view.ScreenView
import com.xfinity.rmvp_sample.mvp.model.DataItemModel
import com.xfinity.rmvp_sample.mvp.model.FooterModel
import com.xfinity.rmvp_sample.mvp.model.HeaderModel
import com.xfinity.rmvp_sample.mvp.model.DynamicScreenModel
import com.xfinity.rmvp_sample.mvp.presenter.DynamicScreenPresenter
import com.xfinity.rmvp_sample.mvp.view.DynamicScreenView
import com.xfinity.rmvp_sample.rmvp.AppComponentRegistry
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class DynamicScreenPresenterTest {
    @Mock internal lateinit var dynamicScreenModel: DynamicScreenModel
    @Mock internal lateinit var mainView: DynamicScreenView
    @Mock internal lateinit var emptyHeaderModel: HeaderModel
    @Mock internal lateinit var footerModel: FooterModel

    val dataItemModels = mutableListOf<DataItemModel>()

    internal var mainPresenter = DynamicScreenPresenter()
    internal var componentEventManager = ComponentEventManager()

    @Before
    fun setup() {
        `when`<ComponentEventManager>(mainView.componentEventManager).thenReturn(componentEventManager)
    }

    @Test
    fun empty_header_is_removed() {
        dataItemModels.add(DataItemModel())

        `when`<String>(emptyHeaderModel.header).thenReturn("")
        `when`<HeaderModel>(dynamicScreenModel.headerModel).thenReturn(emptyHeaderModel)
        `when`<List<DataItemModel>>(dynamicScreenModel.dataItemModels).thenReturn(listOf())
        `when`<FooterModel>(dynamicScreenModel.footerModel).thenReturn(footerModel)
        `when`<List<DataItemModel>>(dynamicScreenModel.dataItemModels).thenReturn(dataItemModels)

        mainPresenter.model = dynamicScreenModel
        mainPresenter.attachView(mainView)
        mainPresenter.present()

        verify<ScreenView>(mainView, never()).addComponent(argThat { viewType == AppComponentRegistry.HeaderView_VIEW_TYPE},
                any(),
                any())
    }

    @Test
    fun default_header_is_added() {
        mainPresenter.attachView(mainView)
        mainPresenter.present()

        val componentCapture = argumentCaptor<List<Component>>()
        verify<ScreenView>(mainView).updateComponents(componentCapture.capture())
        assert(componentCapture.firstValue.find { it.viewType == AppComponentRegistry.HeaderView_VIEW_TYPE } != null)
    }

}
