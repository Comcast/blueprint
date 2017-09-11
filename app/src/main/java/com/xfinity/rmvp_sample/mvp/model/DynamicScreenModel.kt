package com.xfinity.rmvp_sample.mvp.model

open class DynamicScreenModel {
    open var headerModel: HeaderModel = HeaderModel()
    var footerModel: FooterModel = FooterModel()
    open var dataItemModels: MutableList<DataItemModel> = mutableListOf(DataItemModel(), DataItemModel(), DataItemModel(),
            DataItemModel(), DataItemModel(), DataItemModel())
}