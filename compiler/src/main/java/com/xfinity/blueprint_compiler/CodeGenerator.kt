package com.xfinity.blueprint_compiler

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.sun.tools.javac.util.Pair
import java.util.ArrayList
import java.util.Arrays
import java.util.Locale

class CodeGenerator(private val componentViewInfoList: List<BlueprintProcessor.ComponentViewInfo>? = null,
                    private val defaultPresenterConstructorMap: Map<String, List<Pair<TypeName, String>>>? = null) {

    fun generateComponentRegistry(): TypeSpec {
        val properties: MutableList<PropertySpec> = ArrayList()
        val companionProperties: MutableList<PropertySpec> = ArrayList()
        val componentViewWhenStatements: MutableList<String> = ArrayList()
        val defaultPresenterWhenStatements: MutableList<String> = ArrayList()
        componentViewWhenStatements.add("return when(viewType) {\n")
        defaultPresenterWhenStatements.add("return when(viewType) {\n")

        val getDefaultPresenterMethodbuilder1 = FunSpec.builder("getDefaultPresenter")
                .addModifiers(KModifier.PUBLIC)
                .addParameter("componentView", ClassName("com.xfinity.blueprint.view", "ComponentView"))
                .addParameter("args", Object::class, KModifier.VARARG)
                .returns(ClassName("com.xfinity.blueprint.presenter", "ComponentPresenter"))

        val getDefaultPresenterMethodbuilder2 = FunSpec.builder("getDefaultPresenter")
                .addModifiers(KModifier.PUBLIC)
                .addParameter("viewType", INT)
                .addParameter("args", Object::class, KModifier.VARARG)
                .returns(ClassName("com.xfinity.blueprint.presenter", "ComponentPresenter"))

        val contructorArgs: MutableList<Pair<TypeName, String>> = ArrayList()
        for (componentViewInfo in componentViewInfoList!!) {
            val viewTypeFieldName = componentViewInfo.viewTypeName + "_VIEW_TYPE"
            val propertySpec = PropertySpec.builder(viewTypeFieldName, INT, KModifier.PUBLIC,
                    KModifier.FINAL).initializer(componentViewInfo.viewType.toString()).build()
            companionProperties.add(propertySpec)

            componentViewWhenStatements.add("$viewTypeFieldName -> ${componentViewInfo.componentView}\n")

            if (componentViewInfo.defaultPresenter != null) {
                getDefaultPresenterMethodbuilder1.addCode("""
    if (componentView is ${componentViewInfo.componentView}) {
    
    """.trimIndent())
                var returnStatement: String
                val defaultPresenterContructorArgs =
                        if (defaultPresenterConstructorMap != null && componentViewInfo.defaultPresenter != null) {
                            defaultPresenterConstructorMap[componentViewInfo.defaultPresenter!!]
                        } else { null }

                returnStatement = if (defaultPresenterContructorArgs == null) {
                    "return new " + componentViewInfo.defaultPresenter + "()"
                } else {
                    val statementBuilder = StringBuilder("return new " + componentViewInfo.defaultPresenter + "(")
                    for (j in defaultPresenterContructorArgs.indices) {
                        val argPair = defaultPresenterContructorArgs[j]
                        val argName = argPair.snd //arg name
                        statementBuilder.append(argName)
                        if (j < defaultPresenterContructorArgs.size - 1) {
                            statementBuilder.append(", ")
                        } else {
                            statementBuilder.append(")")
                        }

                        //check if an arg with this name and type was already added to the ComponentRegistry's ctor, if
                        // not, add it
                        if (!contructorArgs.contains(argPair)) {
                            contructorArgs.add(argPair)
                        }
                    }
                    statementBuilder.toString()
                }
                getDefaultPresenterMethodbuilder1.addStatement(returnStatement)
                getDefaultPresenterMethodbuilder1.addCode("}\n")
                defaultPresenterWhenStatements.add("case $viewTypeFieldName:\n")
                defaultPresenterWhenStatements.add("$returnStatement;\n")
            }
        }
        val getComponentViewMethodbuilder = FunSpec.builder("getComponentView")
                .addModifiers(KModifier.PUBLIC)
                .addParameter("viewType", INT)
                .returns(ClassName("com.xfinity.blueprint.view", "ComponentView"))
        for (statement in componentViewWhenStatements) {
            getComponentViewMethodbuilder.addCode(statement)
        }
        getComponentViewMethodbuilder.addCode("}\n")
        getComponentViewMethodbuilder.addStatement("return null")
        getDefaultPresenterMethodbuilder1.addStatement("return null")
        for (statement in defaultPresenterWhenStatements) {
            getDefaultPresenterMethodbuilder2.addCode(statement)
        }
        getDefaultPresenterMethodbuilder2.addCode("}\n")
        getDefaultPresenterMethodbuilder2.addStatement("return null")
        val componentRegistryConstructorBuilder = FunSpec.constructorBuilder()
                .addModifiers(KModifier.PUBLIC)
        contructorArgs.sortWith(Comparator() { pair: Pair<TypeName, String>, pair1: Pair<TypeName, String> ->
            pair.fst.toString().compareTo(pair1.fst.toString(), ignoreCase = true)
        })
        for (argPair in contructorArgs) {
            properties.add(PropertySpec.builder(argPair.snd, argPair.fst, KModifier.PRIVATE, KModifier.FINAL).build())
            componentRegistryConstructorBuilder.addParameter(ParameterSpec.builder(argPair.snd, argPair.fst).build())
            componentRegistryConstructorBuilder.addStatement("this." + argPair.snd + " = " + argPair.snd)
        }
        val classBuilder = TypeSpec.classBuilder("AppComponentRegistry")
                .addModifiers(KModifier.PUBLIC)
                .addSuperinterface(ClassName("com.xfinity.blueprint", "ComponentRegistry"))
                .addProperties(properties)
                .addFunction(componentRegistryConstructorBuilder.build())
                .addFunction(getComponentViewMethodbuilder.build())
                .addFunction(getDefaultPresenterMethodbuilder1.build())
                .addFunction(getDefaultPresenterMethodbuilder2.build())
        return classBuilder.build()
    }

    fun generateViewBaseClasses(): List<Pair<String, TypeSpec>> {
        val viewDelegatePairs: MutableList<Pair<String, TypeSpec>> = ArrayList()
        componentViewInfoList?.let {
            for (componentViewInfo in componentViewInfoList) {
                val componentViewPackageName = componentViewInfo.componentView?.let {
                    componentViewInfo.componentView?.substring(0, it.lastIndexOf("."))
                }
                val viewHolderPackageName = componentViewInfo.viewHolder.substring(0, componentViewInfo.viewHolder.lastIndexOf("."))
                val viewHolderName = componentViewInfo.viewHolder.substring(componentViewInfo.viewHolder.lastIndexOf(".") + 1,
                        componentViewInfo.viewHolder.length)
                val viewHolderTypeName: TypeName = ClassName(viewHolderPackageName, viewHolderName)
                var viewBinderTypeName: TypeName?
                viewBinderTypeName = if (componentViewInfo.viewBinder != null && componentViewInfo.viewBinder == BlueprintProcessor.DEFAULT_VIEW_BINDER) {
                    ClassName("com.xfinity.blueprint.view", "ComponentViewBinder")
                } else {
                    ClassName("com.xfinity.blueprint.view", "ComponentViewBinder")
                            .plusParameter(viewHolderTypeName)
                }

                var viewBinderFieldSpec: PropertySpec? = null
                if (componentViewInfo.viewBinder != null) {
                    viewBinderFieldSpec = PropertySpec.builder("viewBinder", viewBinderTypeName, KModifier.PRIVATE,
                            KModifier.FINAL).initializer("${componentViewInfo.viewBinder}()").build()
                }
                val viewHolderFieldSpec = PropertySpec.builder("viewHolder", viewHolderTypeName, KModifier.PRIVATE).build()
                val getViewHolderMethod = FunSpec.builder("getViewHolder")
                        .addModifiers(KModifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addStatement("return viewHolder")
                        .returns(viewHolderTypeName)
                        .build()
                val viewHolderParameterSpec = ParameterSpec.builder("viewHolder", viewHolderTypeName).build()
                val setViewHolderMethod = FunSpec.builder("setViewHolder")
                        .addModifiers(KModifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addParameter(viewHolderParameterSpec)
                        .build()
                val getComponentViewBinderMethodBuilder = FunSpec.builder("getComponentViewBinder")
                        .addModifiers(KModifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .returns(viewBinderTypeName)
                if (componentViewInfo.viewBinder != null) {
                    getComponentViewBinderMethodBuilder.addStatement("return viewBinder")
                } else {
                    getComponentViewBinderMethodBuilder.addStatement("return null")
                }
                val getComponentViewBinderMethod = getComponentViewBinderMethodBuilder.build()
                val viewGroupParam = ParameterSpec.builder("parent", ClassName("android.view", "ViewGroup")).build()
                val onCreateViewHolderMethod = FunSpec.builder("onCreateViewHolder")
                        .addModifiers(KModifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addParameter(viewGroupParam)
                        .addStatement("android.view.View view = android.view.LayoutInflater.from(parent.getContext()).inflate(getViewType(), parent, false)")
                        .addStatement("return new " + componentViewInfo.viewHolder + "(view)")
                        .returns(viewHolderTypeName)
                        .build()

                val componentPresenterParam = ParameterSpec.builder("componentPresenter", ClassName("com.xfinity.blueprint.presenter", "ComponentPresenter")).build()
                val viewHolderParam = ParameterSpec.builder("viewHolder", ClassName("androidx.recyclerview.widget", "RecyclerView").nestedClass("ViewHolder")).build()
                val positionParam = ParameterSpec.builder("position", INT).build()
                val componentViewTypeName: TypeName = ClassName("com.xfinity.blueprint.view", "ComponentView").plusParameter(viewHolderTypeName)

                val onBindViewHolderMethodBuilder = FunSpec.builder("onBindViewHolder")
                        .addModifiers(KModifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addParameters(listOf(componentPresenterParam, viewHolderParam, positionParam))
                        .addCode("""
    if (viewHolder instanceof ${componentViewInfo.viewHolder}) {
    
    """.trimIndent())
                        .addStatement("this.viewHolder = (" + componentViewInfo.viewHolder + ") viewHolder")
                        .addCode("} else {\n")
                        .addStatement("throw new IllegalArgumentException(\"You can only attach $viewHolderName to this view object\")")
                        .addCode("}\n")
                if (componentViewInfo.viewBinder != null) {
                    onBindViewHolderMethodBuilder.addStatement("viewBinder.bind(componentPresenter, this, this.viewHolder, position)")
                }
                val onBindViewHolderMethod = onBindViewHolderMethodBuilder.build()
                val getViewTypeMethod = FunSpec.builder("getViewType")
                        .addModifiers(KModifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .returns(INT)
                        .addStatement("return " + componentViewInfo.viewType)
                        .build()
                val onBindViewHolderMethodFields: MutableList<PropertySpec> = ArrayList()
                if (viewBinderFieldSpec != null) {
                    onBindViewHolderMethodFields.add(viewBinderFieldSpec)
                }
                val methods: MutableList<FunSpec> = ArrayList(Arrays.asList(getViewHolderMethod,
                        setViewHolderMethod,
                        getComponentViewBinderMethod,
                        onCreateViewHolderMethod,
                        onBindViewHolderMethod,
                        getViewTypeMethod))
                componentViewInfo.children?.let {
                    for (child in it.keys) {
                        val type = it[child]
                        val childCapitalized = child.substring(0, 1).toUpperCase(Locale.getDefault()) + child.substring(1)
                        val childGetter = "get$childCapitalized()"
                        if (type == "android.widget.TextView") {
                            methods.add(getSetTextMethodSpec(childCapitalized, childGetter))
                        }
                        if (type == "android.widget.ImageView") {
                            methods.add(getSetImageDrawableMethodSpec(childCapitalized, childGetter))
                        }

                        methods.add(getMakeVisibleMethodSpec(childCapitalized, childGetter))
                        methods.add(getMakeGoneMethodSpec(childCapitalized, childGetter))
                        methods.add(getMakeInvisibleMethodSpec(childCapitalized, childGetter))
                        methods.add(getSetBackgroundColorMethodSpec(childCapitalized, childGetter))
                    }
                }
                onBindViewHolderMethodFields.add(viewHolderFieldSpec)
                val classBuilder = TypeSpec.classBuilder(componentViewInfo.viewTypeName + "Base")
                        .addModifiers(KModifier.PUBLIC)
                        .addSuperinterface(componentViewTypeName)
                        .addProperties(onBindViewHolderMethodFields)
                        .addFunctions(methods)
                viewDelegatePairs.add(Pair(componentViewPackageName ?: "", classBuilder.build()))
            }
        }
    return viewDelegatePairs
}

private fun getSetTextMethodSpec(childNameCapitalized: String, childGetterName: String): FunSpec {
    val textParam = ParameterSpec.builder("text", ClassName("java.lang", "CharSequence")).build()

    //Warning:  this code assumes that fields all have getters, and that they're named getFieldName()
    return FunSpec.builder("set" + childNameCapitalized + "Text")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(textParam)
            .addStatement("viewHolder.$childGetterName.setText(text)")
            .build()
}

private fun getMakeVisibleMethodSpec(childName: String, childGetterName: String): FunSpec {
    return FunSpec.builder("make" + childName + "Visible")
            .addModifiers(KModifier.PUBLIC)
            .addStatement("viewHolder.$childGetterName.setVisibility(android.view.View.VISIBLE)")
            .build()
}

private fun getMakeGoneMethodSpec(childName: String, childGetterName: String): FunSpec {
    return FunSpec.builder("make" + childName + "Gone")
            .addModifiers(KModifier.PUBLIC)
            .addStatement("viewHolder.$childGetterName.setVisibility(android.view.View.GONE)")
            .build()
}

private fun getMakeInvisibleMethodSpec(childName: String, childGetterName: String): FunSpec {
    return FunSpec.builder("make" + childName + "Invisible")
            .addModifiers(KModifier.PUBLIC)
            .addStatement("viewHolder.$childGetterName.setVisibility(android.view.View.INVISIBLE)")
            .build()
}

private fun getSetBackgroundColorMethodSpec(childName: String, childGetterName: String): FunSpec {
    val colorParam = ParameterSpec.builder("color", INT).build()
    return FunSpec.builder("set" + childName + "BackgroundColor")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(colorParam)
            .addStatement("viewHolder.$childGetterName.setBackgroundColor(color)")
            .build()
}

private fun getSetImageDrawableMethodSpec(childName: String, childGetterName: String): FunSpec {
    val imageParam = ParameterSpec.builder("drawable", ClassName("android.graphics.drawable", "Drawable")).build()
    return FunSpec.builder("set" + childName + "Image")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(imageParam)
            .addStatement("viewHolder.$childGetterName.setImageDrawable(drawable)")
            .build()
}
}