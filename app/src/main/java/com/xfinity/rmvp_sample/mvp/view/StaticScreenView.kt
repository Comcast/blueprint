package com.xfinity.rmvp_sample.mvp.view

import com.xfinity.rmvp.view.ScreenView
import com.xfinity.rmvp.view.ScreenViewDelegate

class StaticScreenView(val screenViewDelegate: ScreenViewDelegate) :
        ScreenView by screenViewDelegate