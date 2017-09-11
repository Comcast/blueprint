package com.xfinity.rmvp_sample.mvp.model

import com.xfinity.rmvp.model.ComponentModel

open class HeaderModel : ComponentModel {
    open var header : String = "this is a header"
    open var enabled : Boolean = false
}