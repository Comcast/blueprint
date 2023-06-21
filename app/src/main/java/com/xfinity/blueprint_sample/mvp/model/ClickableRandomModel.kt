package com.xfinity.blueprint_sample.mvp.model

import com.xfinity.blueprint.model.ComponentModel

class ClickableRandomModel: ComponentModel{

    var enabled: Boolean = false
    val text: String = "Clickable!"
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

}