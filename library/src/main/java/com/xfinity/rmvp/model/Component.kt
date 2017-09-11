package com.xfinity.rmvp.model

import com.xfinity.rmvp.presenter.ComponentPresenter

interface ComponentModel

data class Component(val model: ComponentModel, val viewType: Int, val presenter: ComponentPresenter? = null)