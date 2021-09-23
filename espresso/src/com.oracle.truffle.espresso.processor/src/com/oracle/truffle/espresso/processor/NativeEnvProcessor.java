/*
 * Copyright (c) 2017, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.truffle.espresso.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ReferenceType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Handles the generation of boilerplate code for native interface implementations.
 */
public final class NativeEnvProcessor extends EspressoProcessor {
    private static final String JNI_PACKAGE = "com.oracle.truffle.espresso.jni";
    protected static final String FFI_PACKAGE = "com.oracle.truffle.espresso.ffi";
    private static final String POINTER = FFI_PACKAGE + "." + "Pointer";
    private static final String NATIVE_TYPE = FFI_PACKAGE + "." + "NativeType";
    private static final String NATIVE_SIGNATURE = FFI_PACKAGE + "." + "NativeSignature";

    private static final String HANDLE = JNI_PACKAGE + "." + "Handle";

    private static final String SUBSTITUTIONS_PACKAGE = "com.oracle.truffle.espresso.substitutions";
    private static final String SUBSTITUTOR = "CallableFromNative";

    private static final String ENV_ARG_NAME = "env";
    private static final String INVOKE = "invoke(Object " + ENV_ARG_NAME + ", Object[] " + ARGS_NAME + ") {\n";

    private static final String GENERATE_INTRISIFICATION = "com.oracle.truffle.espresso.substitutions.GenerateNativeEnv";

    protected static final String IMPORT_NATIVE_SIGNATURE = NATIVE_SIGNATURE;
    protected static final String IMPORT_NATIVE_TYPE = NATIVE_TYPE;

    // @Pointer
    private TypeElement pointerAnnotation;

    // @Handle
    private TypeElement handleAnnotation;

    private String envPackage;
    private String envClassName;

    private String envName;
    private String imports;

    // @GenerateNativeEnv
    private TypeElement generateIntrinsification;

    private static final class IntrinsificationTarget {
        // The package for the target intrinsified native env.
        private final String envPackage;
        // The simple name of the target intrisified class.
        private final String envClassName;
        // The annotation supported by the target intrinsified class.
        private final TypeElement intrinsicAnnotation;

        IntrinsificationTarget(String envPackage, String envClassName, TypeElement intrinsicAnnotation) {
            this.envPackage = envPackage;
            this.envClassName = envClassName;
            this.intrinsicAnnotation = intrinsicAnnotation;
        }
    }

    private final List<IntrinsificationTarget> targets = new ArrayList<>();

    public static final class IntrinsincsHelper extends SubstitutionHelper {
        final NativeType[] jniNativeSignature;
        final List<Boolean> referenceTypes;
        final boolean isStatic;
        final boolean prependEnv;
        final boolean needsHandlify;

        public IntrinsincsHelper(EspressoProcessor processor,
                        Element element,
                        TypeElement implAnnotation,
                        NativeType[] jniNativeSignature,
                        List<Boolean> referenceTypes,
                        boolean isStatic,
                        boolean prependEnv,
                        boolean needsHandlify) {
            super(processor, element, implAnnotation);
            this.jniNativeSignature = jniNativeSignature;
            this.referenceTypes = referenceTypes;
            this.isStatic = isStatic;
            this.prependEnv = prependEnv;
            this.needsHandlify = needsHandlify;
        }
    }

    public NativeEnvProcessor() {
        super(SUBSTITUTIONS_PACKAGE, SUBSTITUTOR);
    }

    @Override
    void processImpl(RoundEnvironment env) {
        // Set up the different annotations, along with their values, that we will need.
        this.generateIntrinsification = getTypeElement(GENERATE_INTRISIFICATION);
        this.pointerAnnotation = getTypeElement(POINTER);
        this.handleAnnotation = getTypeElement(HANDLE);

        for (Element e : env.getElementsAnnotatedWith(generateIntrinsification)) {
            findIntrisificationTarget(e);
        }
        for (IntrinsificationTarget target : targets) {
            initClosure(target);
            for (Element e : env.getElementsAnnotatedWith(target.intrinsicAnnotation)) {
                processElement(e, target.intrinsicAnnotation);
            }
        }
    }

    private void initClosure(IntrinsificationTarget target) {
        this.envPackage = target.envPackage;
        this.envClassName = target.envClassName;

        this.envName = envClassName.toLowerCase();
        this.imports = envPackage + "." + envClassName + ";\n";
    }

    private void findIntrisificationTarget(Element e) {
        assert e.getKind() == ElementKind.CLASS;
        TypeElement c = (TypeElement) e;
        AnnotationMirror genIntrisification = getAnnotation(c, generateIntrinsification);
        TypeMirror targetAnnotation = getAnnotationValue(genIntrisification, "target", TypeMirror.class);
        String qualifiedName = c.getQualifiedName().toString();
        int lastDot = qualifiedName.lastIndexOf('.');
        String packageName;
        String className;
        if (lastDot > 0) {
            packageName = qualifiedName.substring(0, lastDot);
            className = qualifiedName.substring(lastDot + 1);
        } else {
            packageName = "";
            className = qualifiedName;
        }
        targets.add(new IntrinsificationTarget(packageName, className, asTypeElement(targetAnnotation)));
    }

    void processElement(Element element, TypeElement implAnnotation) {
        assert element.getKind() == ElementKind.METHOD || element.getKind() == ElementKind.CLASS;
        assert element.getEnclosingElement().getKind() == ElementKind.CLASS;
        TypeElement declaringClass = (TypeElement) element.getEnclosingElement();
        String targetPackage = env().getElementUtils().getPackageOf(declaringClass).getQualifiedName().toString();

        AnnotationMirror genIntrisification = getAnnotation(declaringClass, generateIntrinsification);
        boolean prependEnvValue = getAnnotationValue(genIntrisification, "prependEnv", Boolean.class);
        boolean prependEnv = prependEnvValue || isJni(element, implAnnotation);
        String className = envClassName;
        // Obtain the name of the method to be substituted in.
        String substitutedMethodName = getSubstutitutedMethodName(element);

        // This is the actual method that will be called by the substitution.
        ExecutableElement targetMethod = getTargetMethod(element);

        // Obtain the host types of the parameters
        List<String> espressoTypes = new ArrayList<>();
        List<Boolean> referenceTypes = new ArrayList<>();
        boolean needsHandlify = getEspressoTypes(targetMethod, espressoTypes, referenceTypes);
        // Spawn the name of the Substitutor we will create.
        String substitutorName = getSubstitutorClassName(className, substitutedMethodName, espressoTypes);
        // Obtain the jniNativeSignature
        NativeType[] jniNativeSignature = jniNativeSignature(targetMethod, prependEnv);
        // Check if we need to call an instance method
        boolean isStatic = element.getKind() == ElementKind.METHOD && targetMethod.getModifiers().contains(Modifier.STATIC);
        // Spawn helper
        IntrinsincsHelper h = new IntrinsincsHelper(this, element, implAnnotation, jniNativeSignature, referenceTypes, isStatic, prependEnv, needsHandlify);
        // Create the contents of the source file
        String classFile = spawnSubstitutor(
                        targetPackage,
                        className,
                        substitutedMethodName,
                        espressoTypes, h);
        commitSubstitution(element, targetPackage, substitutorName, classFile);
    }

    private static boolean isJni(Element vmElement, TypeElement implAnnotation) {
        AnnotationMirror mirror = getAnnotation(vmElement, implAnnotation);
        try {
            Boolean value = getAnnotationValue(mirror, "isJni", Boolean.class);
            return value != null && value;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    boolean getEspressoTypes(ExecutableElement inner, List<String> parameterTypeNames, List<Boolean> referenceTypes) {
        boolean hasStaticObject = false;
        for (VariableElement parameter : inner.getParameters()) {
            if (isActualParameter(parameter)) {
                String arg = parameter.asType().toString();
                String result = extractSimpleType(arg);
                boolean isRef = parameter.asType() instanceof ReferenceType;
                if (isRef && result.equals("StaticObject")) {
                    hasStaticObject = true;
                }
                parameterTypeNames.add(result);
                referenceTypes.add(isRef);
            }
        }
        return hasStaticObject;
    }

    /**
     * Converts a parameter/return type into Espresso's NativeType, taking into account @Pointer
     * and @Handle annotations.
     *
     * @param typeMirror type to convert
     * @param element used to report proper error locations
     */
    private NativeType extractNativeType(TypeMirror typeMirror, Element element) {
        AnnotationMirror pointer = getAnnotation(typeMirror, pointerAnnotation);
        AnnotationMirror handle = getAnnotation(typeMirror, handleAnnotation);
        if (pointer != null && handle != null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Parameter cannot be be annotated with both %s and %s", pointer, handle), element);
        }
        if (pointer != null) {
            if (typeMirror.getKind().isPrimitive()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Pointer annotation must be used only with 'TruffleObject' parameters/return types.", element);
            }
            return NativeType.POINTER;
        } else if (handle != null) {
            if (typeMirror.getKind() != TypeKind.LONG) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Handle annotation must be used only with 'long' parameters/return types.", element);
            }
            return NativeType.LONG; // word size
        } else {
            return classToType(typeMirror.getKind());
        }
    }

    NativeType[] jniNativeSignature(ExecutableElement method, boolean prependEnv) {
        List<NativeType> signature = new ArrayList<>(16);

        // Return type is always first.
        signature.add(extractNativeType(method.getReturnType(), method));

        // Arguments...

        // Prepend _Env* . The raw pointer will be substituted by the proper `this` reference.
        if (prependEnv) {
            signature.add(NativeType.POINTER);
        }

        for (VariableElement param : method.getParameters()) {
            if (isActualParameter(param)) {
                signature.add(extractNativeType(param.asType(), param));
            }
        }

        return signature.toArray(new NativeType[0]);
    }

    String extractArg(int index, String clazz, boolean isNonPrimitive, int startAt, String tabulation) {
        String decl = tabulation + clazz + " " + ARG_NAME + index + " = ";
        String obj = ARGS_NAME + "[" + (index + startAt) + "]";
        if (isNonPrimitive) {
            if (!clazz.equals("StaticObject")) {
                return decl + castTo(obj, clazz) + ";\n";
            }
            return decl + envName + ".getHandles().get(Math.toIntExact((long) " + obj + "))" + ";\n";
        }
        return decl + castTo(obj, clazz) + ";\n";
    }

    String extractInvocation(String className, String methodName, String nodeMethodName, int nParameters, boolean isStatic, SubstitutionHelper helper) {
        StringBuilder str = new StringBuilder();
        if (helper.isNodeTarget()) {
            str.append("this.node.").append(nodeMethodName).append("(");
        } else {
            if (isStatic) {
                str.append(className).append(".").append(methodName).append("(");
            } else {
                str.append(envName).append(".").append(methodName).append("(");
            }
        }
        boolean first = true;
        for (int i = 0; i < nParameters; i++) {
            first = checkFirst(str, first);
            str.append(ARG_NAME).append(i);
        }
        first = appendInvocationMetaInformation(str, first, helper);
        str.append(")"); // ;\n");
        return str.toString();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(GENERATE_INTRISIFICATION);
        return annotations;
    }

    @Override
    List<String> expectedImports(String className, String targetMethodName, List<String> parameterTypeName, SubstitutionHelper helper) {
        List<String> expectedImports = new ArrayList<>();
        IntrinsincsHelper h = (IntrinsincsHelper) helper;
        expectedImports.add(imports);
        expectedImports.add(IMPORT_NATIVE_SIGNATURE);
        expectedImports.add(IMPORT_NATIVE_TYPE);
        expectedImports.add(substitutorPackage + "." + SUBSTITUTOR);
        if (helper.isNodeTarget()) {
            expectedImports.add(helper.getNodeTarget().getQualifiedName().toString());
        }
        if (helper.hasProfileInjection) {
            expectedImports.add(IMPORT_PROFILE);
        }
        if (parameterTypeName.contains("String")) {
            expectedImports.add(IMPORT_INTEROP_LIBRARY);
        }
        if (parameterTypeName.contains("StaticObject") || h.jniNativeSignature[0].equals(NativeType.VOID)) {
            expectedImports.add(IMPORT_STATIC_OBJECT);
        }
        if (parameterTypeName.contains("TruffleObject")) {
            expectedImports.add(IMPORT_TRUFFLE_OBJECT);
        }
        return expectedImports;
    }

    @Override
    String generateFactoryConstructorAndBody(String className, String targetMethodName, List<String> parameterTypeName, SubstitutionHelper helper) {
        StringBuilder str = new StringBuilder();
        IntrinsincsHelper h = (IntrinsincsHelper) helper;
        str.append(TAB_3).append("super(\n");
        str.append(TAB_4).append(ProcessorUtils.stringify(targetMethodName)).append(",\n");
        str.append(TAB_4).append(generateNativeSignature(h.jniNativeSignature)).append(",\n");
        str.append(TAB_4).append(parameterTypeName.size()).append(",\n");
        str.append(TAB_4).append(h.prependEnv).append("\n");
        str.append(TAB_3).append(");\n");
        str.append(TAB_2).append("}\n");
        return str.toString();
    }

    @Override
    String generateInvoke(String className, String targetMethodName, List<String> parameterTypes, SubstitutionHelper helper) {
        StringBuilder str = new StringBuilder();
        IntrinsincsHelper h = (IntrinsincsHelper) helper;
        str.append(TAB_1).append(PUBLIC_FINAL_OBJECT).append(INVOKE);
        if (h.needsHandlify || !h.isStatic) {
            str.append(TAB_2).append(envClassName).append(" ").append(envName).append(" = ").append("(").append(envClassName).append(") " + ENV_ARG_NAME + ";\n");
        }
        int argIndex = 0;
        for (String type : parameterTypes) {
            boolean isNonPrimitive = h.referenceTypes.get(argIndex);
            str.append(extractArg(argIndex++, type, isNonPrimitive, h.prependEnv ? 1 : 0, TAB_2));
        }
        String nodeMethodName = null;
        if (helper.isNodeTarget()) {
            ExecutableElement nodeExecute = findNodeExecute(helper.getNodeTarget());
            nodeMethodName = nodeExecute.getSimpleName().toString();
        }
        switch (h.jniNativeSignature[0]) {
            case VOID:
                str.append(TAB_2).append(extractInvocation(className, targetMethodName, nodeMethodName, argIndex, h.isStatic, helper)).append(";\n");
                str.append(TAB_2).append("return ").append(STATIC_OBJECT_NULL).append(";\n");
                break;
            case OBJECT:
                str.append(TAB_2).append("return ").append(
                                "(long) " + envName + ".getHandles().createLocal(" + extractInvocation(className, targetMethodName, nodeMethodName, argIndex, h.isStatic, helper) + ")").append(";\n");
                break;
            default:
                str.append(TAB_2).append("return ").append(extractInvocation(className, targetMethodName, nodeMethodName, argIndex, h.isStatic, helper)).append(";\n");
        }
        str.append(TAB_1).append("}\n");
        str.append("}");
        return str.toString();
    }
}