package com.xfinity.rmvp_sample.mvp.model

import com.xfinity.rmvp.model.ComponentModel

class DataItemModel : ComponentModel {
    val data : String = "This is some data"
    var enabled = false

    override fun equals(other: Any?): Boolean {
        return other is DataItemModel && other.enabled == enabled
    }
}