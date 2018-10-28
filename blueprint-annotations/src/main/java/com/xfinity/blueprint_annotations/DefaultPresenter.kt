package com.xfinity.blueprint_annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class DefaultPresenter(val viewClass: KClass<*>)

//TODO if the 'library' project were a straight java project, we could enforce stricter rules on this annotation,
// i.e. val viewClass: KClass<out ComponentView>
