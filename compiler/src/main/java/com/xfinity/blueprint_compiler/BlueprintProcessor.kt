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

import com.google.auto.common.MoreElements.getPackage
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.internal.ClassInspectorUtil
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import com.xfinity.blueprint_annotations.ComponentViewClass
import com.xfinity.blueprint_annotations.ComponentViewHolder
import com.xfinity.blueprint_annotations.DefaultPresenter
import com.xfinity.blueprint_annotations.DefaultPresenterConstructor
import kotlinx.metadata.KmClassifier
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.MirroredTypeException
import javax.tools.Diagnostic

@AutoService(Processor::class)
class BlueprintProcessor : AbstractProcessor() {
    private val messager = Messager()
    private var outputPackageName: String? = null
    private var appPackageName: String? = null

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager.init(processingEnv)
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        val annotations: MutableSet<String> = LinkedHashSet()
        annotations.add(ComponentViewClass::class.java.canonicalName)
        annotations.add(ComponentViewHolder::class.java.canonicalName)
        annotations.add(DefaultPresenter::class.java.canonicalName)
        annotations.add(DefaultPresenterConstructor::class.java.canonicalName)
        return annotations
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    @KotlinPoetMetadataPreview
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        var viewHolderPackageName: String? = null

        val componentViewInfoList: MutableList<ComponentViewInfo> = ArrayList()
        for (annotatedElement in roundEnv.getElementsAnnotatedWith(ComponentViewHolder::class.java)) { // annotation is only allowed on classes, so we can safely cast here
            val annotatedClass = annotatedElement as TypeElement

            if (!isValidClass(annotatedClass)) {
                continue
            }

            if (viewHolderPackageName == null && appPackageName == null && outputPackageName == null) {
                try {
                    viewHolderPackageName = processingEnv.elementUtils.getPackageName(annotatedClass)
                } catch (e: UnnamedPackageException) {
                    e.printStackTrace()
                }

                //Epic hackery
                if (viewHolderPackageName != null) {
                    val packageNameTokens = viewHolderPackageName.split(".")
                    appPackageName = if (packageNameTokens.size >= 3) {
                        "${packageNameTokens[0]}.${packageNameTokens[1]}.${packageNameTokens[2]}"
                    } else {
                        viewHolderPackageName
                    }

                    outputPackageName = "$appPackageName.blueprint"
                }
            }

            val viewType = annotatedElement.getAnnotation(ComponentViewHolder::class.java).viewType
            val viewHolderClassName = getFullClassName(annotatedClass)
            val children = mutableMapOf<String, String>()
            val methodNames = mutableListOf<String>()

            for (enclosedElement in annotatedElement.getEnclosedElements()) { // We only look at fields
                if (enclosedElement.kind == ElementKind.METHOD) {
                    methodNames.add(enclosedElement.simpleName.toString().toLowerCase())
                }
            }
            for (enclosedElement in annotatedElement.getEnclosedElements()) { // We only look at fields
                if (enclosedElement.kind.isField) {
                    val variable = enclosedElement as VariableElement
                    val variableName = enclosedElement.getSimpleName().toString()

                    //we'll only generate methods for this field if this class has
                    // a getter for the field. We'll assume a naming convention of getFieldName()
                    var hasGetter = false
                    for (methodName in methodNames) {
                        if (methodName == "get${variableName.toLowerCase()}") {
                            hasGetter = true
                            break
                        }
                    }
                    if (!hasGetter) {
                        continue
                    }
                    if (isEditText(variable)) {
                        children[variableName] = "android.widget.EditText"
                    } else if (isTextView(variable)) {
                        children[variableName] = "android.widget.TextView"
                    } else if (isImageView(variable)) {
                        children[variableName] = "android.widget.ImageView"
                    } else if (isAndroidView(variable)) {
                        children[variableName] = "android.view.View"
                    }
                }
            }
            val componentViewInfo = ComponentViewInfo(viewType, viewHolderClassName)
            componentViewInfo.children = children
            componentViewInfoList.add(componentViewInfo)
        }
        for (annotatedElement in roundEnv.getElementsAnnotatedWith(ComponentViewClass::class.java)) {
            val annotatedClass = annotatedElement as TypeElement
            var viewHolderClassName: String? = null
            try {
                annotatedElement.getAnnotation(ComponentViewClass::class.java).viewHolderClass
            } catch (exception: MirroredTypeException) {
                val typeMirror = exception.typeMirror
                viewHolderClassName = typeMirror.toString()
            }
            for (componentViewInfo in componentViewInfoList) {
                if (viewHolderClassName == componentViewInfo.viewHolder) {
                    componentViewInfo.componentView = annotatedClass.asType().toString()
                    componentViewInfo.viewTypeName = annotatedClass.simpleName.toString()
                }
            }
        }
        for (annotatedElement in roundEnv.getElementsAnnotatedWith(DefaultPresenter::class.java)) {
            val annotatedClass = annotatedElement as TypeElement
            var viewClassName: String? = null //TODO : validate class
            try {
                annotatedElement.getAnnotation(DefaultPresenter::class.java).viewClass
            } catch (exception: MirroredTypeException) {
                val typeMirror = exception.typeMirror
                viewClassName = typeMirror.toString()
            }
            for (componentViewInfo in componentViewInfoList) {
                if (viewClassName == componentViewInfo.componentView) {
                    componentViewInfo.defaultPresenter = getFullClassName(annotatedClass)
                }
            }
        }

        //a Map of Presenters to a List of their Constructor parameters names and classes
        val defaultPresenterConstructorMap: MutableMap<String, List<Pair<ClassName, String>>> = HashMap()
        for (annotatedElement in roundEnv.getElementsAnnotatedWith(
            DefaultPresenterConstructor::class.java)) { //            val parameters = (annotatedElement as ExecutableElement).parameters
            val constructors = annotatedElement.enclosingElement.getAnnotation(Metadata::class.java).toImmutableKmClass().constructors
            if (constructors.isNotEmpty()) {
                val parameters = constructors[0].valueParameters
                val ctorParamNameAndTypeList = mutableListOf<Pair<ClassName, String>>()
                for (parameter in parameters) {
                    val paramName = parameter.name
                    val className = (parameter.type?.classifier as? KmClassifier.Class)?.name.toString()
                    val paramClass = ClassInspectorUtil.createClassName(className)
                    ctorParamNameAndTypeList.add(Pair(paramClass, paramName))
                }
                val presenterClassName = getFullClassName(annotatedElement.enclosingElement as TypeElement)
                defaultPresenterConstructorMap[presenterClassName] = ctorParamNameAndTypeList
            }
        }

        appPackageName?.let { appPackageName ->
            outputPackageName?.let { outputPackageName ->
                if (componentViewInfoList.isNotEmpty() || defaultPresenterConstructorMap.isNotEmpty()) {
                    try {
                        generateCode(appPackageName, outputPackageName, componentViewInfoList, defaultPresenterConstructorMap)
                    } catch (e: Exception) {
                        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, e.stackTrace.toString())
                    }
                }
            }
        }

        return true
    }

    @KotlinPoetMetadataPreview
    private fun getFullClassName(element: TypeElement): String {
        val typeMetadata = element.getAnnotation(Metadata::class.java)
        val kmClass = typeMetadata.toImmutableKmClass()
        return kmClass.name.replace("/", ".")
    }

    private fun isAndroidView(variable: VariableElement): Boolean {
        val viewElement = processingEnv.elementUtils.getTypeElement("android.view.View")
        return processingEnv.typeUtils.isAssignable(variable.asType(), viewElement.asType())
    }

    private fun isTextView(variable: VariableElement): Boolean {
        val textViewElement = processingEnv.elementUtils.getTypeElement("android.widget.TextView")
        return processingEnv.typeUtils.isAssignable(variable.asType(), textViewElement.asType())
    }

    private fun isEditText(variable: VariableElement): Boolean {
        val textViewElement = processingEnv.elementUtils.getTypeElement("android.widget.EditText")
        return processingEnv.typeUtils.isAssignable(variable.asType(), textViewElement.asType())
    }

    private fun isImageView(variable: VariableElement): Boolean {
        val textViewElement = processingEnv.elementUtils.getTypeElement("android.widget.ImageView")
        return processingEnv.typeUtils.isAssignable(variable.asType(), textViewElement.asType())
    }

    private fun isValidClass(annotatedClass: TypeElement): Boolean {
        val applicationTypeElement = processingEnv.elementUtils.getTypeElement("androidx.recyclerview.widget.RecyclerView.ViewHolder")
        return processingEnv.typeUtils.isAssignable(annotatedClass.asType(), applicationTypeElement.asType())
    }

    @Throws(IOException::class) private fun generateCode(appPackageName: String, outputPackageName: String, componentInfoList: List<ComponentViewInfo>,
                                                         defaultPresenterConstructorMap: Map<String, List<Pair<ClassName, String>>>) {
        val codeGenerator = CodeGenerator(appPackageName, componentInfoList, defaultPresenterConstructorMap)
        val generatedClass = codeGenerator.generateComponentRegistry()

        val kotlinFile = generatedClass.name?.let {
            FileSpec.builder(outputPackageName, it).addType(generatedClass)
                .addImport("androidx.recyclerview.widget", "RecyclerView")
                .build()
        }

        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "\n\n\n component registry code \n\n" +
                "$generatedClass")

        try {
            kotlinFile?.writeTo(processingEnv.filer)
        } catch (e: Exception) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, e.toString())
        }

        val viewDelegates = codeGenerator.generateViewBaseClasses()
        for (viewDelegate in viewDelegates) {
            viewDelegate.second.name?.let {
                val viewDelegateKotlinFile = FileSpec.builder(viewDelegate.first, it).addType(viewDelegate.second).build()
                try {
                    viewDelegateKotlinFile.writeTo(processingEnv.filer)
                } catch (e: Exception) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, e.toString())
                }
            }
        }
    }

    data class ComponentViewInfo(val viewType: String, val viewHolder: String) {
        var viewTypeName: String? = null
        var defaultPresenter: String? = null
        var componentView: String? = null
        var children: Map<String, String>? = null
    }
}