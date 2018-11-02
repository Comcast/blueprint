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

package com.xfinity.blueprint_sample

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.xfinity.blueprint.event.ComponentEventManager
import com.xfinity.blueprint.model.Component
import com.xfinity.blueprint.view.ScreenView
import com.xfinity.blueprint_sample.blueprint.AppComponentRegistry
import com.xfinity.blueprint_sample.mvp.model.DataItemModel
import com.xfinity.blueprint_sample.mvp.model.DynamicScreenModel
import com.xfinity.blueprint_sample.mvp.model.FooterModel
import com.xfinity.blueprint_sample.mvp.model.HeaderModel
import com.xfinity.blueprint_sample.mvp.presenter.DynamicScreenPresenter
import com.xfinity.blueprint_sample.mvp.view.DefaultDynamicScreenView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class DynamicScreenPresenterTest {
    @Mock private lateinit var dynamicScreenModel: DynamicScreenModel
    @Mock private lateinit var mainView: DefaultDynamicScreenView
    @Mock private lateinit var emptyHeaderModel: HeaderModel
    @Mock private lateinit var footerModel: FooterModel

    private val dataItemModels = mutableListOf<DataItemModel>()

    private lateinit var mainPresenter: DynamicScreenPresenter

    @Before
    fun setup() {
        mainPresenter = DynamicScreenPresenter(ComponentEventManager())
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
