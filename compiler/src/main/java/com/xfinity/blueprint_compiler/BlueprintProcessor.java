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

package com.xfinity.blueprint_compiler;

import static com.squareup.javapoet.JavaFile.builder;
import static javax.lang.model.SourceVersion.latestSupported;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xfinity.blueprint_annotations.ClickableComponentBinder;
import com.xfinity.blueprint_annotations.ComponentViewClass;
import com.xfinity.blueprint_annotations.ComponentViewHolder;
import com.xfinity.blueprint_annotations.ComponentViewHolderBinder;
import com.xfinity.blueprint_annotations.DefaultPresenter;
import com.xfinity.blueprint_annotations.DefaultPresenterConstructor;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
public class BlueprintProcessor extends AbstractProcessor {
    public static final String DEFAULT_VIEW_BINDER = "com.xfinity.blueprint.view.ClickableComponentViewBinder";

    private final Messager messager = new Messager();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(ComponentViewClass.class.getCanonicalName());
        annotations.add(ComponentViewHolder.class.getCanonicalName());
        annotations.add(ComponentViewHolderBinder.class.getCanonicalName());
        annotations.add(DefaultPresenter.class.getCanonicalName());
        annotations.add(DefaultPresenterConstructor.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        StringBuilder packageName = null;
        String appPackageName = null;
        List<ComponentViewInfo> componentViewInfoList = new ArrayList<>();

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ComponentViewHolder.class)) {
            // annotation is only allowed on classes, so we can safely cast here
            TypeElement annotatedClass = (TypeElement) annotatedElement;
            if (!isValidClass(annotatedClass)) {
                continue;
            }

            if (packageName == null) {
                try {
                    appPackageName = Utils.getPackageName(processingEnv.getElementUtils(), annotatedClass);
                    packageName = new StringBuilder(appPackageName);
                } catch (UnnamedPackageException e) {
                    e.printStackTrace();
                }

                //Epic hackery
                if (packageName != null) {
                    String[] packageNameTokens = packageName.toString().split("\\.");
                    if (packageNameTokens.length >= 3) {
                        appPackageName = packageNameTokens[0] + "." + packageNameTokens[1] + "." + packageNameTokens[2];
                        packageName = new StringBuilder(appPackageName);
                    }

                    packageName.append(".blueprint");
                }
            }

            String viewType = annotatedElement.getAnnotation(ComponentViewHolder.class).viewType();

            String viewHolderClassName = getFullClassName(annotatedClass);

            Map<String, String> children = new HashMap<>();
            List<String> methodNames = new ArrayList<>();
            for (Element enclosedElement : annotatedElement.getEnclosedElements()) {
                // We only look at fields
                if (enclosedElement.getKind() == ElementKind.METHOD) {
                    methodNames.add(enclosedElement.getSimpleName().toString().toLowerCase());
                }
            }

            for (Element enclosedElement : annotatedElement.getEnclosedElements()) {
                // We only look at fields
                if (enclosedElement.getKind().isField()) {
                    VariableElement variable = (VariableElement) enclosedElement;
                    String variableName = enclosedElement.getSimpleName().toString();

                    //we'll only generate methods for this field if this class has
                    // a getter for the field. We'll assume a naming convention of getFieldName()
                    boolean hasGetter = false;
                    for (String methodName : methodNames) {
                        if (methodName.equals("get" + variableName.toLowerCase())) {
                            hasGetter = true;
                            break;
                        }
                    }

                    if (!hasGetter) {
                        continue;
                    }

                    if (isEditText(variable)) {
                        children.put(variableName, "android.widget.EditText");
                    } else if (isTextView(variable)) {
                        children.put(variableName, "android.widget.TextView");
                    } else if (isImageView(variable)) {
                        children.put(variableName, "android.widget.ImageView");
                    } else if (isAndroidView(variable)) {
                        children.put(variableName, "android.view.View");
                    }
                }
            }

            ComponentViewInfo componentViewInfo = new ComponentViewInfo(viewType, viewHolderClassName);
            componentViewInfo.children = children;
            componentViewInfoList.add(componentViewInfo);
        }

//        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ComponentViewHolderBinder.class)) {
//            TypeElement annotatedClass = (TypeElement) annotatedElement;
//            TypeElement param = (TypeElement) annotatedClass.getTypeParameters().get(0);
//            String viewHolderClassName = getFullClassName(param);
//
//            for (ComponentViewInfo componentViewInfo : componentViewInfoList) {
//                if (viewHolderClassName.equals(componentViewInfo.viewHolder)) {
//                    componentViewInfo.viewBinder = getFullClassName(annotatedClass);
//                }
//            }
//        }

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ComponentViewClass.class)) {
            TypeElement annotatedClass = (TypeElement) annotatedElement;
            String viewHolderClassName = null;
            try {
                annotatedElement.getAnnotation(ComponentViewClass.class).viewHolderClass();
            } catch (MirroredTypeException exception) {
                TypeMirror typeMirror = exception.getTypeMirror();
                viewHolderClassName = typeMirror.toString();
            }

            boolean useDefaultBinder = annotatedClass.getAnnotationsByType(ClickableComponentBinder.class).length > 0;

            for (ComponentViewInfo componentViewInfo : componentViewInfoList) {
                if (viewHolderClassName.equals(componentViewInfo.viewHolder)) {
                    componentViewInfo.componentView = annotatedClass.asType().toString();
                    componentViewInfo.viewTypeName = annotatedClass.getSimpleName().toString();

                    if (componentViewInfo.viewBinder == null && useDefaultBinder) {
                        componentViewInfo.viewBinder = DEFAULT_VIEW_BINDER;
                    }
                }
            }
        }

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(DefaultPresenter.class)) {
            TypeElement annotatedClass = (TypeElement) annotatedElement;
            String viewClassName = null;
            //TODO : validate class

            try {
                annotatedElement.getAnnotation(DefaultPresenter.class).viewClass();
            } catch (MirroredTypeException exception) {
                TypeMirror typeMirror = exception.getTypeMirror();
                viewClassName = typeMirror.toString();
            }

            for (ComponentViewInfo componentViewInfo : componentViewInfoList) {
                if (viewClassName.equals(componentViewInfo.componentView)) {
                    componentViewInfo.defaultPresenter = getFullClassName(annotatedClass);
                }
            }
        }

        //a Map of Presenters to a List of there Constructor parameters names and classes
        Map<String, List<Pair<TypeMirror, String>>> defaultPresenterConstructorMap = new HashMap<>();
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(DefaultPresenterConstructor.class)) {
//            Symbol.MethodSymbol annotatedCtor = (Symbol.MethodSymbol) annotatedElement;
            List<? extends VariableElement> parameters = ((ExecutableElement) annotatedElement).getParameters();
            List<Pair<TypeMirror, String>> ctorParamNameAndTypeList = new ArrayList();
            for (VariableElement parameter : parameters) {
                String paramName = parameter.getSimpleName().toString();
                TypeMirror paramClass = parameter.asType();
                ctorParamNameAndTypeList.add(new ImmutablePair(paramClass, paramName));
            }


//            annotatedElement.getClass().getTypeParameters()[0].getName()
//            ExecutableType executableType = (ExecutableType)annotatedElement.asType();


//            List<? extends TypeMirror> parameters = executableType.getParameterTypes();
//            List<Pair<TypeName, String>> ctorParams = new ArrayList();
//            for (TypeMirror typeMirror : parameters) {
//                TypeName typeName = ClassName.bestGuess(typeMirror.toString());
//                String className = typeMirror.
//                ctorParams.add(new ImmutablePair(typeName, className));
//            }

//            List<Pair<TypeName, String>> ctorParams = annotatedClass.getTypeParameters()
//                    .stream()
//                    .map(param -> new Pair<TypeName, String>(TypeName.get(param.asType()), param.getSimpleName().toString())).collect(Collectors.toList());

            String presenterClassName = getFullClassName((TypeElement) annotatedElement.getEnclosingElement());
//            Symbol.ClassSymbol presenterClass = (Symbol.ClassSymbol) annotatedCtor.owner;
//            defaultPresenterConstructorMap.put(presenterClass.fullname.toString(), ctorParams);
            defaultPresenterConstructorMap.put(presenterClassName, ctorParamNameAndTypeList);
        }

        if (packageName != null) {
            try {
                generateCode(appPackageName, packageName.toString(), componentViewInfoList, defaultPresenterConstructorMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private String getFullClassName(TypeElement element) {
        ClassName className = ClassName.get(element);
        return className.packageName() + "." + className.simpleName();
    }

    private boolean isAndroidView(VariableElement variable) {
        TypeElement viewElement = processingEnv.getElementUtils().getTypeElement("android.view.View");
        return processingEnv.getTypeUtils().isAssignable(variable.asType(), viewElement.asType());
    }

    private boolean isTextView(VariableElement variable) {
        TypeElement textViewElement = processingEnv.getElementUtils().getTypeElement("android.widget.TextView");
        return processingEnv.getTypeUtils().isAssignable(variable.asType(), textViewElement.asType());
    }

    private boolean isEditText(VariableElement variable) {
        TypeElement textViewElement = processingEnv.getElementUtils().getTypeElement("android.widget.EditText");
        return processingEnv.getTypeUtils().isAssignable(variable.asType(), textViewElement.asType());
    }

    private boolean isImageView(VariableElement variable) {
        TypeElement textViewElement = processingEnv.getElementUtils().getTypeElement("android.widget.ImageView");
        return processingEnv.getTypeUtils().isAssignable(variable.asType(), textViewElement.asType());
    }

    private boolean isValidClass(TypeElement annotatedClass) {
        TypeElement applicationTypeElement = processingEnv.getElementUtils().getTypeElement("androidx.recyclerview.widget.RecyclerView.ViewHolder");
        return processingEnv.getTypeUtils().isAssignable(annotatedClass.asType(), applicationTypeElement.asType());
    }

    private void generateCode(String appPackageName, String packageName, List<ComponentViewInfo> componentInfoList,
                              Map<String, List<Pair<TypeMirror, String>>> defaultPresenterContructorMap)
            throws IOException {
        CodeGenerator codeGenerator =
                new CodeGenerator(appPackageName, componentInfoList, defaultPresenterContructorMap);
        TypeSpec generatedClass = codeGenerator.generateComponentRegistry();

        JavaFile javaFile = builder(packageName, generatedClass).build();
        javaFile.writeTo(processingEnv.getFiler());


        List<Pair<String, TypeSpec>> viewDelegates = codeGenerator.generateViewBaseClasses();
        for (Pair<String, TypeSpec> viewDelegate : viewDelegates) {
            JavaFile file = builder(viewDelegate.getLeft(), viewDelegate.getRight()).build();
            file.writeTo(processingEnv.getFiler());
        }
    }

    class ComponentViewInfo {
        final String viewType;
        final String viewHolder;
        String viewTypeName;
        String defaultPresenter;
        String componentView;
        String viewBinder;
        Map<String, String> children;

        ComponentViewInfo(String viewType, String viewHolder) {
            this.viewType = viewType;
            this.viewHolder = viewHolder;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ComponentViewInfo that = (ComponentViewInfo) o;

            return viewType.equals(that.viewType);
        }

        @Override
        public int hashCode() {
            return viewType.hashCode();
        }
    }
}

