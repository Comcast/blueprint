package com.xfinity.blueprint_annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class ComponentViewClass(val viewHolderClass: KClass<*>)
