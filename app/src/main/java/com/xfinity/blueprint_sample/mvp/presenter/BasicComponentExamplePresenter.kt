package com.xfinity.blueprint_sample.mvp.presenter

import com.xfinity.blueprint.architecture.component.BasicComponentModel
import com.xfinity.blueprint.architecture.component.BasicComponentPresenter
import com.xfinity.blueprint.presenter.ComponentPresenter
import com.xfinity.blueprint_annotations.DefaultPresenter
import com.xfinity.blueprint_sample.mvp.view.BasicComponentExample

/**
 * This is an example of a Basic Component Presenter.  Since every potential view in the Basic
 * Component has logic governing its presentation in the BasicComponentPresenter, no code is needed
 * here, and all the testing for this component is covered by the BasicComponentPresenterTest
 */
@DefaultPresenter(viewClass = BasicComponentExample::class)
class BasicComponentExamplePresenter :
    ComponentPresenter<BasicComponentExample, BasicComponentModel> by
    BasicComponentPresenter()