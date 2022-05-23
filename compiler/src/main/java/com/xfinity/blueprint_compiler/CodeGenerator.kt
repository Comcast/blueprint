/*
 * Copyright 2017 Comcast Cable Communications Management, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xfinity.blueprint_compiler

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.xfinity.blueprint_compiler.BlueprintProcessor.ComponentViewInfo
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

internal class CodeGenerator(private val packageName: String, private val componentViewInfoList: List<ComponentViewInfo>,
                             private val defaultPresenterConstructorMap: Map<String, List<Pair<ClassName, String>>>) {
    fun generateComponentRegistry(): TypeSpec {
        val properties = mutableListOf<PropertySpec>()
        val companionProperties = mutableListOf<PropertySpec>()

        val componentViewWhenStatements = mutableListOf<String>()
        val defaultPresenterWhenStatements = mutableListOf<String>()
        componentViewWhenStatements.add("return when(viewType) {\n")
        defaultPresenterWhenStatements.add("return when(viewType) {\n")

        val starProjection = WildcardTypeName.producerOf(ClassName("kotlin", "Any").copy(true))
        val viewHolderProjection = WildcardTypeName.producerOf(ClassName("androidx.recyclerview.widget", "RecyclerView.ViewHolder"))

        val componentViewType: TypeName = ClassName("com.xfinity.blueprint.view", "ComponentView").plusParameter(starProjection)
        val componentViewType2: TypeName = ClassName("com.xfinity.blueprint.view", "ComponentView").plusParameter(viewHolderProjection)

        val nullableComponentPresenterType =
            ClassName("com.xfinity.blueprint.presenter", "ComponentPresenter")
                .parameterizedBy(componentViewType, ClassName("com.xfinity.blueprint.model","ComponentModel"))
                .copy(true)

        val getDefaultPresenterMethodBuilder1 = FunSpec.builder("getDefaultPresenter")
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("componentView", componentViewType2)
            .addParameter("args", Object::class, KModifier.VARARG)
            .addAnnotation(AnnotationSpec.builder(Suppress::class)
                .addMember("%S", "UNCHECKED_CAST")
                .build())
            .returns(nullableComponentPresenterType)

        val getDefaultPresenterMethodBuilder2 = FunSpec.builder("getDefaultPresenter")
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("viewType", INT)
            .addParameter("args", Object::class, KModifier.VARARG)
            .addAnnotation(AnnotationSpec.builder(Suppress::class)
                .addMember("%S", "UNCHECKED_CAST")
                .build())
            .returns(nullableComponentPresenterType)

        getDefaultPresenterMethodBuilder1.addCode("return when(componentView) {\n")

        val constructorArgs = mutableListOf<Pair<ClassName, String>>()
        for (componentViewInfo in componentViewInfoList) {
            val viewTypeFieldName = "${componentViewInfo.viewTypeName}_VIEW_TYPE"
            val propertySpec = PropertySpec.builder(viewTypeFieldName, INT)
                .initializer("$packageName.R.layout.${componentViewInfo.viewType}").build()
            companionProperties.add(propertySpec)

            //here's where we are
            componentViewWhenStatements.add("$viewTypeFieldName -> ${componentViewInfo.componentView}() \n as? ComponentView<RecyclerView.ViewHolder>\n")
            if (componentViewInfo.defaultPresenter != null) {
                getDefaultPresenterMethodBuilder1.addCode("is ${componentViewInfo.componentView} ->".trimIndent())
                var returnStatement: String
                val defaultPresenterConstructorArgs =
                    componentViewInfo.defaultPresenter?.let { defaultPresenterConstructorMap[it] }

                returnStatement = if (defaultPresenterConstructorArgs == null) {
                    "${componentViewInfo.defaultPresenter}()"
                } else {
                    val statementBuilder = StringBuilder("${componentViewInfo.defaultPresenter}(")
                    for (j in defaultPresenterConstructorArgs.indices) {
                        val argPair = defaultPresenterConstructorArgs[j]
                        val argName = argPair.second //arg name
                        statementBuilder.append(argName)
                        if (j < defaultPresenterConstructorArgs.size - 1) {
                            statementBuilder.append(", ")
                        } else {
                            statementBuilder.append(")")
                        }

                        //check if an arg with this name and type was already added to the ComponentRegistry's ctor, if
                        // not, add it
                        if (!constructorArgs.contains(argPair)) {
                            constructorArgs.add(argPair)
                        }
                    }
                    statementBuilder.toString()
                }
                getDefaultPresenterMethodBuilder1.addStatement("$returnStatement as? ComponentPresenter<ComponentView<*>, ComponentModel>?")
                //                    getDefaultPresenterMethodbuilder1.addCode("}\n")
                defaultPresenterWhenStatements.add("$viewTypeFieldName -> \n")
                defaultPresenterWhenStatements.add("$returnStatement as? ComponentPresenter<ComponentView<*>, ComponentModel>?\n")
            }
        }

        val companion = TypeSpec.companionObjectBuilder()
            .addProperties(companionProperties)
            .build()

        val nullableComponentViewType = ClassName("com.xfinity.blueprint.view", "ComponentView").parameterizedBy(
            ClassName("androidx.recyclerview.widget", "RecyclerView.ViewHolder")).copy(true)

        val getComponentViewMethodbuilder = FunSpec.builder("getComponentView")
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("viewType", INT)
            .addAnnotation(AnnotationSpec.builder(Suppress::class)
                .addMember("%S", "UNCHECKED_CAST")
                .build())
            .returns(nullableComponentViewType)

        for (statement in componentViewWhenStatements) {
            getComponentViewMethodbuilder.addCode(statement)
        }
        getComponentViewMethodbuilder.addCode("else -> null\n")
        getComponentViewMethodbuilder.addCode("}\n")

        getDefaultPresenterMethodBuilder1.addCode("else -> null\n")
        getDefaultPresenterMethodBuilder1.addCode("}\n")

        for (statement in defaultPresenterWhenStatements) {
            getDefaultPresenterMethodBuilder2.addCode(statement)
        }

        getDefaultPresenterMethodBuilder2.addCode("else -> null\n")
        getDefaultPresenterMethodBuilder2.addCode("}\n")

        val componentRegistryConstructorBuilder = FunSpec.constructorBuilder()
        constructorArgs.sortWith(Comparator { pair: Pair<ClassName, String>, pair1: Pair<ClassName, String> ->
            pair.first.toString().compareTo(pair1.first.toString(), ignoreCase = true)
        })

        for (argPair in constructorArgs) {
            properties.add(PropertySpec.builder(argPair.second, argPair.first, KModifier.PRIVATE).initializer(argPair.second).build())
            componentRegistryConstructorBuilder.addParameter(ParameterSpec.builder(argPair.second, argPair.first).build())
        }

        val classBuilder = TypeSpec.classBuilder("AppComponentRegistry")
            .addModifiers(KModifier.PUBLIC)
            .addSuperinterface(ClassName("com.xfinity.blueprint", "ComponentRegistry"))
            .addProperties(properties)
            .addType(companion)
            .primaryConstructor(componentRegistryConstructorBuilder.build())
            .addFunction(getComponentViewMethodbuilder.build())
            .addFunction(getDefaultPresenterMethodBuilder1.build())
            .addFunction(getDefaultPresenterMethodBuilder2.build())

        return classBuilder.build()
    }

    fun generateViewBaseClasses(): List<Pair<String, TypeSpec>> {
        val viewDelegatePairs: MutableList<Pair<String, TypeSpec>> = ArrayList()
        for (componentViewInfo in componentViewInfoList) {
            val componentViewPackageName = componentViewInfo.componentView?.let {
                componentViewInfo.componentView?.substring(0, it.lastIndexOf("."))
            }
            val viewHolderPackageName = componentViewInfo.viewHolder.substring(0, componentViewInfo.viewHolder.lastIndexOf("."))
            val viewHolderName =
                componentViewInfo.viewHolder.substring(componentViewInfo.viewHolder.lastIndexOf(".") + 1, componentViewInfo.viewHolder.length)
            val viewHolderTypeName: TypeName = ClassName(viewHolderPackageName, viewHolderName)

            val viewHolderPropertySpec =
                PropertySpec.builder("viewHolder", viewHolderTypeName, KModifier.LATEINIT, KModifier.OVERRIDE).mutable().build()
            val viewGroupParam = ParameterSpec.builder("parent", ClassName("android.view", "ViewGroup")).build()
            val onCreateViewHolderMethod =
                FunSpec.builder("onCreateViewHolder").addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE).addParameter(viewGroupParam)
                    .addStatement("val view = android.view.LayoutInflater.from(parent.getContext()).inflate(getViewType(), parent, false)")
                    .addStatement("return ${componentViewInfo.viewHolder}(view)").returns(viewHolderTypeName).build()

            val starProjection = WildcardTypeName.producerOf(ClassName("kotlin", "Any").copy(true))
            val componentViewTypeName = ClassName("com.xfinity.blueprint.view", "ComponentView")
            val wildcardComponentViewTypeName: TypeName = ClassName("com.xfinity.blueprint.view", "ComponentView").plusParameter(starProjection)

            val componentPresenterParam = ParameterSpec.builder("componentPresenter",
                ClassName("com.xfinity.blueprint.presenter", "ComponentPresenter").plusParameter(wildcardComponentViewTypeName)
                    .plusParameter(ClassName("com.xfinity.blueprint.model", "ComponentModel"))).build()

            val viewHolderParam =
                ParameterSpec.builder("viewHolder", ClassName("androidx.recyclerview.widget", "RecyclerView").nestedClass("ViewHolder")).build()
            val positionParam = ParameterSpec.builder("position", INT).build()
            val parameterizedComponentViewTypeName: TypeName = componentViewTypeName.plusParameter(viewHolderTypeName)

            val onBindViewHolderMethodBuilder = FunSpec.builder("onBindViewHolder").addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .addParameters(listOf(componentPresenterParam, viewHolderParam, positionParam)).addCode("""
    if (viewHolder is ${componentViewInfo.viewHolder}) {
    
    """.trimIndent()).addStatement("this.viewHolder = viewHolder as ${componentViewInfo.viewHolder}").addCode("} else {\n")
                .addStatement("throw IllegalArgumentException(\"You can only attach $viewHolderName to this view\")").addCode("}\n")

            val onBindViewHolderMethod = onBindViewHolderMethodBuilder.build()
            val getViewTypeMethod = FunSpec.builder("getViewType").addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE).returns(INT)
                .addStatement("return $packageName.R.layout.${componentViewInfo.viewType}").build()
            val onBindViewHolderMethodFields = mutableListOf<PropertySpec>()

            val methods = mutableListOf(onCreateViewHolderMethod, onBindViewHolderMethod, getViewTypeMethod)

            componentViewInfo.children?.let {
                for (child in it.keys) {
                    val type = it[child]
                    val childCapitalized = child.substring(0, 1).toUpperCase(Locale.getDefault()) + child.substring(1)
                    if (type == "android.widget.TextView") {
                        methods.add(getSetTextMethodSpec(child, childCapitalized))
                    }
                    if (type == "android.widget.ImageView") {
                        methods.add(getSetDrawableMethodSpec(child, childCapitalized))
                        methods.add(getSetResourceMethodSpec(child, childCapitalized))
                    }

                    methods.add(getMakeVisibleMethodSpec(child, childCapitalized))
                    methods.add(getMakeGoneMethodSpec(child, childCapitalized))
                    methods.add(getMakeInvisibleMethodSpec(child, childCapitalized))
                    methods.add(getSetBackgroundColorMethodSpec(child, childCapitalized))
                }
            }
            onBindViewHolderMethodFields.add(viewHolderPropertySpec)
            val classBuilder = TypeSpec.classBuilder(componentViewInfo.viewTypeName + "Base").addModifiers(KModifier.PUBLIC, KModifier.OPEN)
                .addSuperinterface(parameterizedComponentViewTypeName).addProperties(onBindViewHolderMethodFields).addFunctions(methods)
            viewDelegatePairs.add(Pair(componentViewPackageName ?: "", classBuilder.build()))
        }
        return viewDelegatePairs
    }

    private fun getSetTextMethodSpec(childName: String, childNameCapitalized: String): FunSpec {
        val textParam = ParameterSpec.builder("text", ClassName("kotlin", "CharSequence")).build()

        //Warning:  this code assumes that fields all have getters, and that they're named getFieldName()
        return FunSpec.builder("set" + childNameCapitalized + "Text")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(textParam)
            .addStatement("viewHolder.$childName.text = text")
            .build()
    }

    private fun getMakeVisibleMethodSpec(childName: String, childNameCapitalized: String): FunSpec {
        return FunSpec.builder("make" + childNameCapitalized + "Visible")
            .addModifiers(KModifier.PUBLIC)
            .addStatement("viewHolder.$childName.visibility = android.view.View.VISIBLE")
            .build()
    }

    private fun getMakeGoneMethodSpec(childName: String, childNameCapitalized: String): FunSpec {
        return FunSpec.builder("make" + childNameCapitalized + "Gone")
            .addModifiers(KModifier.PUBLIC)
            .addStatement("viewHolder.$childName.visibility = android.view.View.GONE")
            .build()
    }

    private fun getMakeInvisibleMethodSpec(childName: String, childNameCapitalized: String): FunSpec {
        return FunSpec.builder("make" + childNameCapitalized + "Invisible")
            .addModifiers(KModifier.PUBLIC)
            .addStatement("viewHolder.$childName.visibility = android.view.View.INVISIBLE")
            .build()
    }

    private fun getSetBackgroundColorMethodSpec(childName: String, childNameCapitalized: String): FunSpec {
        val colorParam = ParameterSpec.builder("color", INT).build()
        return FunSpec.builder("set" + childNameCapitalized + "BackgroundColor")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(colorParam)
            .addStatement("viewHolder.$childName.setBackgroundColor(color)")
            .build()
    }

    private fun getSetDrawableMethodSpec(childName: String, childNameCapitalized: String): FunSpec {
        val imageParam = ParameterSpec.builder("drawable", ClassName("android.graphics.drawable", "Drawable")).build()
        return FunSpec.builder("set${childNameCapitalized}Drawable")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(imageParam)
            .addStatement("viewHolder.$childName.setImageDrawable(drawable)")
            .build()
    }

    private fun getSetResourceMethodSpec(childName: String, childNameCapitalized: String): FunSpec {
        val intParam = ParameterSpec.builder("resourceId", ClassName("kotlin", "Int")).build()
        return FunSpec.builder("set${childNameCapitalized}Resource")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(intParam)
            .addStatement("viewHolder.$childName.setImageResource(resourceId)")
            .build()
    }
}