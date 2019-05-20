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

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.Pair;
import com.xfinity.blueprint_annotations.ComponentViewClass;
import com.xfinity.blueprint_annotations.ClickableComponentBinder;
import com.xfinity.blueprint_annotations.DefaultPresenter;
import com.xfinity.blueprint_annotations.DefaultPresenterConstructor;
import com.xfinity.blueprint_annotations.ComponentViewHolder;
import com.xfinity.blueprint_annotations.ComponentViewHolderBinder;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.squareup.javapoet.JavaFile.builder;
import static com.xfinity.blueprint_compiler.Utils.getPackageName;
import static javax.lang.model.SourceVersion.latestSupported;

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
        List<ComponentViewInfo> componentViewInfoList = new ArrayList<>();

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ComponentViewHolder.class)) {
            // annotation is only allowed on classes, so we can safely cast here
            TypeElement annotatedClass = (TypeElement) annotatedElement;
            if (!isValidClass(annotatedClass)) {
                continue;
            }

            if (packageName == null) {
                try {
                    packageName = new StringBuilder(Utils.getPackageName(processingEnv.getElementUtils(), annotatedClass));
                } catch (UnnamedPackageException e) {
                    e.printStackTrace();
                }

                //Epic hackery
                if (packageName != null) {
                    String[] packageNameTokens = packageName.toString().split("\\.");
                    if (packageNameTokens.length >= 3) {
                        packageName = new StringBuilder(packageNameTokens[0] + "." + packageNameTokens[1] + "." + packageNameTokens[2]);
                    }

                    packageName.append(".blueprint");
                }
            }

            int viewType = annotatedElement.getAnnotation(ComponentViewHolder.class).viewType();

            String viewHolderClassName = ((Symbol.ClassSymbol) annotatedClass).fullname.toString();

            Map<String, String> children = new HashMap<>();
            List<String> methodNames = new ArrayList<>();
            for (Element enclosedElement : annotatedElement.getEnclosedElements()) {
                // We only look at fields
                if (enclosedElement instanceof Symbol.MethodSymbol) {
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

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ComponentViewHolderBinder.class)) {
            TypeElement annotatedClass = (TypeElement) annotatedElement;
            String viewHolderClassName = ((Type.ClassType) annotatedClass.getInterfaces().get(0))
                    .typarams_field.get(0).toString();

            for (ComponentViewInfo componentViewInfo : componentViewInfoList) {
                if (viewHolderClassName.equals(componentViewInfo.viewHolder)) {
                    componentViewInfo.viewBinder = ((Symbol.ClassSymbol) annotatedClass).fullname.toString();
                }
            }
        }

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
                    componentViewInfo.componentView = ((Symbol.ClassSymbol) annotatedClass).fullname.toString();
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
                    componentViewInfo.defaultPresenter = ((Symbol.ClassSymbol) annotatedClass).fullname.toString();
                }
            }
        }

        Map<String, List<Pair<TypeName, String>>> defaultPresenterConstructorMap = new HashMap<>();
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(DefaultPresenterConstructor.class)) {
            Symbol.MethodSymbol annotatedCtor = (Symbol.MethodSymbol) annotatedElement;

            List<Pair<TypeName, String>> ctorParams = annotatedCtor.params
                    .stream()
                    .map(param -> new Pair<>(TypeName.get(param.type), param.name.toString())).collect(Collectors.toList());

            Symbol.ClassSymbol presenterClass = (Symbol.ClassSymbol) annotatedCtor.owner;
            defaultPresenterConstructorMap.put(presenterClass.fullname.toString(), ctorParams);
        }

        if (packageName != null) {
            try {
                generateCode(packageName.toString(), componentViewInfoList, defaultPresenterConstructorMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
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

    private void generateCode(String packageName, List<ComponentViewInfo> componentInfoList,
                              Map<String, List<Pair<TypeName, String>>> defaultPresenterContructorMap)
            throws IOException {
        CodeGenerator codeGenerator =
                new CodeGenerator(componentInfoList, defaultPresenterContructorMap);
        TypeSpec generatedClass = codeGenerator.generateComponentRegistry();

        JavaFile javaFile = builder(packageName, generatedClass).build();
        javaFile.writeTo(processingEnv.getFiler());


        List<Pair<String, TypeSpec>> viewDelegates = codeGenerator.generateViewBaseClasses();
        for (Pair<String, TypeSpec> viewDelegate : viewDelegates) {
            JavaFile file = builder(viewDelegate.fst, viewDelegate.snd).build();
            file.writeTo(processingEnv.getFiler());
        }
    }

    class ComponentViewInfo {
        final int viewType;
        final String viewHolder;
        String viewTypeName;
        String defaultPresenter;
        String componentView;
        String viewBinder;
        Map<String, String> children;

        ComponentViewInfo(int viewType, String viewHolder) {
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

            return viewType == that.viewType;
        }

        @Override
        public int hashCode() {
            return viewType;
        }
    }
}

