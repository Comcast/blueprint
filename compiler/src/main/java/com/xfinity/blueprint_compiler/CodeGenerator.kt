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

import com.squareup.javapoet.*
import com.xfinity.blueprint_compiler.BlueprintProcessor.ComponentViewInfo
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.Pair
import java.util.*
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror

internal class CodeGenerator(private val packageName: String, private val componentViewInfoList: List<ComponentViewInfo>,
                             private val defaultPresenterConstructorMap: Map<String, List<Pair<TypeMirror, String>>>) {
    fun generateComponentRegistry(): TypeSpec {
        val fields = mutableListOf<FieldSpec>()
        val componentViewIfStatements = mutableListOf<String>()
        val defaultPresenterIfStatements = mutableListOf<String>()
        val objectVarArgsType = ArrayTypeName.get(Array<Any>::class.java)
        val parameterSpec = ParameterSpec.builder(objectVarArgsType, "args").build()

        val getDefaultPresenterMethodBuilder1 = MethodSpec.methodBuilder("getDefaultPresenter").addModifiers(Modifier.PUBLIC)
            .addParameter(ClassName.get("com.xfinity.blueprint.view", "ComponentView"), "componentView").addParameter(parameterSpec)
            .returns(ClassName.get("com.xfinity.blueprint.presenter", "ComponentPresenter"))

        val getDefaultPresenterMethodBuilder2 =
            MethodSpec.methodBuilder("getDefaultPresenter").addModifiers(Modifier.PUBLIC).addParameter(TypeName.INT, "viewType")
                .addParameter(parameterSpec).returns(ClassName.get("com.xfinity.blueprint.presenter", "ComponentPresenter"))

        val constructorArgs = mutableListOf<Pair<TypeMirror, String>>()
        for (componentViewInfo in componentViewInfoList) {
            val viewTypeFieldName = "${componentViewInfo.viewTypeName}_VIEW_TYPE"
            val fieldSpec = FieldSpec.builder(TypeName.INT, viewTypeFieldName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$packageName.R.layout.${componentViewInfo.viewType}").build()

            fields.add(fieldSpec)
            componentViewIfStatements.add("if (viewType == $viewTypeFieldName) {\n")
            componentViewIfStatements.add("return new ${componentViewInfo.componentView}();\n}".trimIndent())

            componentViewInfo.defaultPresenter?.let { defaultPresenter ->
                getDefaultPresenterMethodBuilder1.addCode("if (componentView instanceof ${componentViewInfo.componentView}) {\n".trimIndent())
                val defaultPresenterConstructorArgs = defaultPresenterConstructorMap[defaultPresenter]
                val returnStatement = if (defaultPresenterConstructorArgs == null) {
                    "return new $defaultPresenter()"
                } else {
                    val statementBuilder = StringBuilder("return new $defaultPresenter(")
                    for (j in defaultPresenterConstructorArgs.indices) {
                        val argPair = defaultPresenterConstructorArgs[j]
                        val argName = argPair.right //arg name
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
                getDefaultPresenterMethodBuilder1.addStatement(returnStatement)
                getDefaultPresenterMethodBuilder1.addCode("}\n")
                defaultPresenterIfStatements.add("if (viewType == $viewTypeFieldName) {\n")
                defaultPresenterIfStatements.add("$returnStatement;\n}")
            }
        }

        val getComponentViewMethodbuilder =
            MethodSpec.methodBuilder("getComponentView").addModifiers(Modifier.PUBLIC).addParameter(TypeName.INT, "viewType")
                .returns(ClassName.get("com.xfinity.blueprint.view", "ComponentView"))

        for (statement in componentViewIfStatements) {
            getComponentViewMethodbuilder.addCode(statement)
        }
        getComponentViewMethodbuilder.addStatement("return null")
        getDefaultPresenterMethodBuilder1.addStatement("return null")

        for (statement in defaultPresenterIfStatements) {
            getDefaultPresenterMethodBuilder2.addCode(statement)
        }

        getDefaultPresenterMethodBuilder2.addStatement("return null")

        val componentRegistryConstructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)

        constructorArgs.sortBy { it.left.toString().toLowerCase(Locale.getDefault()) }

        for (argPair in constructorArgs) {
            val typeName = ClassName.get(argPair.left)
            fields.add(FieldSpec.builder(typeName, argPair.right).addModifiers(Modifier.PRIVATE).addModifiers(Modifier.FINAL).build())
            componentRegistryConstructorBuilder.addParameter(ParameterSpec.builder(typeName, argPair.right).build())
            componentRegistryConstructorBuilder.addStatement("this.${argPair.right} = ${argPair.right}")
        }
        val classBuilder = TypeSpec.classBuilder("AppComponentRegistry").addModifiers(Modifier.PUBLIC)
            .addSuperinterface(ClassName.get("com.xfinity.blueprint", "ComponentRegistry")).addFields(fields)
            .addMethod(componentRegistryConstructorBuilder.build()).addMethod(getComponentViewMethodbuilder.build())
            .addMethod(getDefaultPresenterMethodBuilder1.build()).addMethod(getDefaultPresenterMethodBuilder2.build())
        return classBuilder.build()
    }

    fun generateViewBaseClasses(): List<Pair<String?, TypeSpec?>> {
        val viewDelegatePairs = mutableListOf<Pair<String?, TypeSpec?>>()
        for (componentViewInfo in componentViewInfoList) {
            val componentViewPackageName = componentViewInfo.componentView?.let { componentView ->
                componentView.substring(0, componentView.lastIndexOf("."))
            }

            val viewHolderPackageName = componentViewInfo.viewHolder.substring(0, componentViewInfo.viewHolder.lastIndexOf("."))

            val viewHolderName =
                componentViewInfo.viewHolder.substring(componentViewInfo.viewHolder.lastIndexOf(".") + 1, componentViewInfo.viewHolder.length)

            val viewHolderTypeName: TypeName = ClassName.get(viewHolderPackageName, viewHolderName)
            val viewHolderFieldSpec = FieldSpec.builder(viewHolderTypeName, "viewHolder", Modifier.PRIVATE).build()
            val notNullAnnotation = ClassName.get("org.jetbrains.annotations", "NotNull")
            val getViewHolderMethod = MethodSpec.methodBuilder("getViewHolder").addModifiers(Modifier.PUBLIC).addAnnotation(notNullAnnotation)
                .addAnnotation(Override::class.java).addStatement("return viewHolder").returns(viewHolderTypeName).build()
            val viewHolderParameterSpec = ParameterSpec.builder(viewHolderTypeName, "viewHolder").build()
            val setViewHolderMethod = MethodSpec.methodBuilder("setViewHolder").addModifiers(Modifier.PUBLIC).addAnnotation(notNullAnnotation)
                .addAnnotation(Override::class.java).addParameter(viewHolderParameterSpec).build()
            val viewGroupParam = ParameterSpec.builder(ClassName.get("android.view", "ViewGroup"), "parent").addAnnotation(notNullAnnotation).build()

            val onCreateViewHolderMethod =
                MethodSpec.methodBuilder("onCreateViewHolder").addModifiers(Modifier.PUBLIC).addAnnotation(notNullAnnotation)
                    .addAnnotation(Override::class.java).addParameter(viewGroupParam).addStatement(
                        "android.view.View view = android.view.LayoutInflater.from(parent.getContext()).inflate(getViewType(), parent, false)")
                    .addStatement("return new ${componentViewInfo.viewHolder}(view)").returns(viewHolderTypeName).build()

            val componentPresenterParam =
                ParameterSpec.builder(ClassName.get("com.xfinity.blueprint.presenter", "ComponentPresenter"), "componentPresenter")
                    .addAnnotation(notNullAnnotation).build()

            val viewHolderParam =
                ParameterSpec.builder(ClassName.get("androidx.recyclerview.widget", "RecyclerView").nestedClass("ViewHolder"), "viewHolder")
                    .addAnnotation(notNullAnnotation).build()

            val positionParam = ParameterSpec.builder(TypeName.INT, "position").addAnnotation(notNullAnnotation).build()

            val componentViewTypeName: TypeName =
                ParameterizedTypeName.get(ClassName.get("com.xfinity.blueprint.view", "ComponentView"), viewHolderTypeName)

            val onBindViewHolderMethodBuilder =
                MethodSpec.methodBuilder("onBindViewHolder").addModifiers(Modifier.PUBLIC).addAnnotation(Override::class.java)
                    .addParameters(Arrays.asList(componentPresenterParam, viewHolderParam, positionParam))
                    .addCode("if (viewHolder instanceof ${componentViewInfo.viewHolder}) {\n")
                    .addStatement("this.viewHolder = (${componentViewInfo.viewHolder}) viewHolder").addCode("} else {\n")
                    .addStatement("throw new IllegalArgumentException(\"You can only attach $viewHolderName to this view object\")").addCode("}\n")

            val onBindViewHolderMethod = onBindViewHolderMethodBuilder.build()
            val getViewTypeMethod =
                MethodSpec.methodBuilder("getViewType").addModifiers(Modifier.PUBLIC).addAnnotation(Override::class.java).returns(TypeName.INT)
                    .addStatement("return ${packageName}.R.layout.${componentViewInfo.viewType}").build()

            val onBindViewHolderMethodFields = mutableListOf<FieldSpec>()
            val methods = mutableListOf<MethodSpec>(getViewHolderMethod, setViewHolderMethod, onCreateViewHolderMethod, onBindViewHolderMethod,
                getViewTypeMethod)

            componentViewInfo.children?.let { children ->
                for (child in children.keys) {
                    val type = children[child]
                    val childCapitalized = child.substring(0, 1).toUpperCase() + child.substring(1)
                    val childGetter = "get$childCapitalized()"
                    if (type == "android.widget.TextView") {
                        methods.add(getSetTextMethodSpec(childCapitalized, childGetter))
                    }
                    if (type == "android.widget.ImageView") {
                        methods.add(getSetDrawableMethodSpec(childCapitalized, childGetter))
                        methods.add(getSetImageResourceMethodSpec(childCapitalized, childGetter))
                    }
                    methods.add(getSetVisibilityMethodSpec(childCapitalized, childGetter))
                    methods.add(getMakeVisibleMethodSpec(childCapitalized, childGetter))
                    methods.add(getMakeGoneMethodSpec(childCapitalized, childGetter))
                    methods.add(getMakeInvisibleMethodSpec(childCapitalized, childGetter))
                    methods.add(getSetBackgroundColorMethodSpec(childCapitalized, childGetter))
                }
            }

            onBindViewHolderMethodFields.add(viewHolderFieldSpec)
            val classBuilder =
                TypeSpec.classBuilder("${componentViewInfo.viewTypeName}Base").addModifiers(Modifier.PUBLIC).addSuperinterface(componentViewTypeName)
                    .addFields(onBindViewHolderMethodFields).addMethods(methods)
            viewDelegatePairs.add(ImmutablePair(componentViewPackageName, classBuilder.build()))
        }
        return viewDelegatePairs
    }

    private fun getSetTextMethodSpec(childNameCapitalized: String, childGetterName: String): MethodSpec {
        val textParam = ParameterSpec.builder(ClassName.get("java.lang", "CharSequence"), "text").build()

        //Warning:  this code assumes that fields all have getters, and that they're named getFieldName()
        return MethodSpec.methodBuilder("set${childNameCapitalized}Text").addModifiers(Modifier.PUBLIC).addParameter(textParam)
            .returns(TypeName.VOID).addStatement("viewHolder.$childGetterName.setText(text)").build()
    }

    private fun getSetVisibilityMethodSpec(childName: String, childGetterName: String): MethodSpec {
        val visibilityParam = ParameterSpec.builder(TypeName.INT, "visibility").build()
        val deprecatedAnnotation = AnnotationSpec.builder(ClassName.get("java.lang", "Deprecated")).build()
        return MethodSpec.methodBuilder("set${childName}Visibility")
            .addJavadoc("@deprecated.  Use make\$LVisible(), make\$LInvisible(), or make\$LGone() instead", childName, childName, childName)
            .addAnnotation(deprecatedAnnotation).addModifiers(Modifier.PUBLIC).addParameter(visibilityParam).returns(TypeName.VOID)
            .addStatement("viewHolder.$childGetterName.setVisibility(visibility)").build()
    }

    private fun getMakeVisibleMethodSpec(childName: String, childGetterName: String): MethodSpec {
        return MethodSpec.methodBuilder("make${childName}Visible").addModifiers(Modifier.PUBLIC).returns(TypeName.VOID)
            .addStatement("viewHolder.$childGetterName.setVisibility(android.view.View.VISIBLE)").build()
    }

    private fun getMakeGoneMethodSpec(childName: String, childGetterName: String): MethodSpec {
        return MethodSpec.methodBuilder("make${childName}Gone").addModifiers(Modifier.PUBLIC).returns(TypeName.VOID)
            .addStatement("viewHolder.$childGetterName.setVisibility(android.view.View.GONE)").build()
    }

    private fun getMakeInvisibleMethodSpec(childName: String, childGetterName: String): MethodSpec {
        return MethodSpec.methodBuilder("make${childName}Invisible").addModifiers(Modifier.PUBLIC).returns(TypeName.VOID)
            .addStatement("viewHolder.$childGetterName.setVisibility(android.view.View.INVISIBLE)").build()
    }

    private fun getSetBackgroundColorMethodSpec(childName: String, childGetterName: String): MethodSpec {
        val colorParam = ParameterSpec.builder(TypeName.INT, "color").build()
        return MethodSpec.methodBuilder("set${childName}BackgroundColor").addModifiers(Modifier.PUBLIC).addParameter(colorParam)
            .returns(TypeName.VOID).addStatement("viewHolder.$childGetterName.setBackgroundColor(color)").build()
    }

    private fun getSetDrawableMethodSpec(childName: String, childGetterName: String): MethodSpec {
        val imageParam = ParameterSpec.builder(ClassName.get("android.graphics.drawable", "Drawable"), "drawable").build()
        return MethodSpec.methodBuilder("set${childName}Drawable").addModifiers(Modifier.PUBLIC).addParameter(imageParam).returns(TypeName.VOID)
            .addStatement("viewHolder.$childGetterName.setImageDrawable(drawable)").build()
    }

    private fun getSetImageResourceMethodSpec(childName: String, childGetterName: String): MethodSpec {
        val imageParam = ParameterSpec.builder(TypeName.INT, "resourceId").build()
        return MethodSpec.methodBuilder("set${childName}Resource").addModifiers(Modifier.PUBLIC).addParameter(imageParam).returns(TypeName.VOID)
            .addStatement("viewHolder.$childGetterName.setImageResource(resourceId)").build()
    }
}