package com.xfinity.blueprint_annotations


/**
 * This annotation should be applied to a ComponentView, to indicate that it should be assigned a
 * com.xfinity.rmvp.view.ClickableComponentViewBinder, which calls through to the ComponentPresenter's onComponentClicked
 * function, when the user clicks on the itemView of the ComponentView's ViewHolder
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class ClickableComponentBinder
