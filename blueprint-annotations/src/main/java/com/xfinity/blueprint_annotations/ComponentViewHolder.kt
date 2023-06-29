package com.xfinity.blueprint_annotations


@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class ComponentViewHolder(val viewType: String)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class BasicComponent
