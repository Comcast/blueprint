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

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.xfinity.blueprint_annotations.ComponentViewClass
import com.xfinity.blueprint_annotations.ComponentViewHolder
import com.xfinity.blueprint_annotations.DefaultPresenter
import com.xfinity.blueprint_annotations.DefaultPresenterConstructor
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.Pair
import java.io.IOException
import java.util.Locale
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror


@AutoService(Processor::class)
class BlueprintProcessor : AbstractProcessor() {
    private val messager = Messager()

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

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        var packageName: StringBuilder? = null
        var appPackageName: String? = null
        val componentViewInfoList: MutableList<ComponentViewInfo> = ArrayList()
        for (annotatedElement in roundEnv.getElementsAnnotatedWith(
            ComponentViewHolder::class.java)) { // annotation is only allowed on classes, so we can safely cast here
            val annotatedClass = annotatedElement as TypeElement
            if (!isValidClass(annotatedClass)) {
                continue
            }

            if (packageName == null) {
                try {
                    appPackageName = processingEnv.elementUtils.getPackageName(annotatedClass)
                    packageName = StringBuilder(appPackageName)
                } catch (e: UnnamedPackageException) {
                    e.printStackTrace()
                }

                //Epic hackery
                if (packageName != null) {
                    val packageNameTokens = packageName.toString().split(".")
                    if (packageNameTokens.size >= 3) {
                        appPackageName = "${packageNameTokens[0]}.${packageNameTokens[1]}.${packageNameTokens[2]}"
                        packageName = StringBuilder(appPackageName)
                    }
                    packageName.append(".blueprint")
                }
            }

            val viewType = annotatedElement.getAnnotation(ComponentViewHolder::class.java).viewType
            val viewHolderClassName = getFullClassName(annotatedClass)
            val children = mutableMapOf<String, String>()
            val methodNames = mutableListOf<String>()

            for (supertype in processingEnv.typeUtils.directSupertypes(annotatedElement.asType())) {
                val declared = supertype as DeclaredType
                val supertypeElement: Element = declared.asElement()
                processViewHolderElement(supertypeElement, methodNames, children)
            }

            processViewHolderElement(annotatedElement, methodNames, children)

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

        //a Map of Presenters to a List of there Constructor parameters names and classes
        val defaultPresenterConstructorMap: MutableMap<String, List<Pair<TypeMirror, String>>> = HashMap()
        for (annotatedElement in roundEnv.getElementsAnnotatedWith(DefaultPresenterConstructor::class.java)) {
            val parameters = (annotatedElement as ExecutableElement).parameters
            val ctorParamNameAndTypeList = mutableListOf<Pair<TypeMirror, String>>()
            for (parameter in parameters) {
                val paramName = parameter.simpleName.toString()
                val paramClass = parameter.asType()
                ctorParamNameAndTypeList.add(ImmutablePair(paramClass, paramName))
            }
            val presenterClassName = getFullClassName(annotatedElement.getEnclosingElement() as TypeElement)
            defaultPresenterConstructorMap[presenterClassName] = ctorParamNameAndTypeList
        }

        if (packageName != null && appPackageName != null) {
            try {
                generateCode(appPackageName, packageName.toString(), componentViewInfoList, defaultPresenterConstructorMap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return true
    }

    private fun processViewHolderElement(element: Element, methodNames: MutableList<String>,
                                         children: MutableMap<String, String>) {
        for (enclosedElement in element.enclosedElements) {
            if (enclosedElement.kind == ElementKind.METHOD) {
                methodNames.add(enclosedElement.simpleName.toString().lowercase(Locale.getDefault()))
            }
        }

        if (methodNames.isNotEmpty()) {
            for (enclosedElement in element.enclosedElements) {
                if (enclosedElement.kind.isField) {
                    val variable = enclosedElement as VariableElement
                    val variableName = enclosedElement.getSimpleName().toString()

                    //we'll only generate methods for this field if this class has
                    // a getter for the field. We'll assume a naming convention of getFieldName()
                    var hasGetter = false
                    for (methodName in methodNames) {
                        if (methodName == "get${variableName.lowercase(Locale.getDefault())}") {
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
        }
    }

    private fun getFullClassName(element: TypeElement): String {
        val className = ClassName.get(element)
        return "${className.packageName()}.${className.simpleName()}"
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

    @Throws(IOException::class)
    private fun generateCode(appPackageName: String, packageName: String, componentInfoList: List<ComponentViewInfo>,
                             defaultPresenterConstructorMap: Map<String, List<Pair<TypeMirror, String>>>) {
        val codeGeneratorJava = CodeGenerator(appPackageName, componentInfoList, defaultPresenterConstructorMap)
        val generatedClass = codeGeneratorJava.generateComponentRegistry()
        val javaFile = JavaFile.builder(packageName, generatedClass).build()
        javaFile.writeTo(processingEnv.filer)
        val viewDelegates = codeGeneratorJava.generateViewBaseClasses()
        for (viewDelegate in viewDelegates) {
            val file = JavaFile.builder(viewDelegate.left, viewDelegate.right).build()
            file.writeTo(processingEnv.filer)
        }
    }

    data class ComponentViewInfo(val viewType: String, val viewHolder: String) {
        var viewTypeName: String? = null
        var defaultPresenter: String? = null
        var componentView: String? = null
        var children: Map<String, String>? = null
    }
}