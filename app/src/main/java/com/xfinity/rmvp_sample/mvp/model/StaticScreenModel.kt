package com.xfinity.rmvp_sample.mvp.model

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