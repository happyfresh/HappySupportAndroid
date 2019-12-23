package com.happyfresh.happyrouter.processor;

import com.google.common.collect.ImmutableSet;
import com.happyfresh.happyrouter.annotations.Extra;
import com.happyfresh.happyrouter.annotations.Route;
import com.happyfresh.happyrouter.annotations.SaveExtra;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class RouterProcessor extends AbstractProcessor {

    private static final String ROUTER_METHOD_PREFIX = "put";

    private static final String ROUTER_CLASS_SUFFIX = "Router";

    private static final String BINDER_CLASS_SUFFIX = "_ExtrasBinding";

    private static final String ROUTER_CONFIG_EXTRAS_PREFIX = "EXTRAS_";

    private static final ClassName typeClassContext = ClassName.get("android.content", "Context");

    private static final ClassName typeClassIntent = ClassName.get("android.content", "Intent");

    private static final ClassName typeClassBundle = ClassName.get("android.os", "Bundle");

    private static final ClassName typeClassBaseRouter = ClassName.get("com.happyfresh.happyrouter", "BaseRouter");

    private static final ClassName typeClassRouterConfig = ClassName.get("com.happyfresh.happyrouter", "RouterConfig");

    private static final ClassName typeClassBaseFragmentRouter = ClassName
            .get("com.happyfresh.happyrouter", "BaseFragmentRouter");

    private static final ClassName typeClassExtrasBinding = ClassName
            .get("com.happyfresh.happyrouter", "ExtrasBinding");

    private static final ClassName typeClassParcelable = ClassName.get("android.os", "Parcelable");

    private static final String bundlePutStatement = "intent.putExtra(\"%1$s\", %2$s)";

    private static final String bundlePutIntegerArrayListStatement = "intent.putIntegerArrayListExtra(\"%1$s\", (java.util.ArrayList<%2$s>) %3$s)";

    private static final String bundlePutStringArrayListStatement = "intent.putStringArrayListExtra(\"%1$s\", (java.util.ArrayList<%2$s>) %3$s)";

    private static final String bundlePutCharSequenceArrayListStatement = "intent.putCharSequenceArrayListExtra(\"%1$s\", (java.util.ArrayList<%2$s>) %3$s)";

    private static final String bundlePutParcelableArrayListStatement = "intent.putParcelableArrayListExtra(\"%1$s\", (java.util.ArrayList<%2$s>) %3$s)";

    private static final String bundleGetStatement = "get(\"%1$s\", %2$s, %3$s)";

    private Filer filer;

    private Messager messager;

    private Elements elements;

    private Types types;

    private Set<String> extraKeys;

    private Map<TypeElement, TypeElement> classWithParents;

    private Map<TypeElement, Map<Element, TypeMirror>> classWithFields;

    private Map<TypeElement, Map<Element, TypeMirror>> classWithSaveFields;

    private Map<TypeElement, Map<Element, Extra>> classWithExtras;

    private Map<TypeElement, Map<Element, SaveExtra>> classWithSaveExtras;

    private Map<TypeElement, TypeElement> requiredClassWithParents;

    private Map<TypeElement, Map<Element, TypeMirror>> requiredClassWithFields;

    private TypeMirror fragmentTypeMirror;

    private TypeMirror fragmentV4TypeMirror;

    private TypeMirror parcelableTypeMirror;

    private TypeMirror serializableTypeMirror;

    private TypeMirror listTypeMirror;

    private TypeMirror integerTypeMirror;

    private TypeMirror charSequenceTypeMirror;

    private TypeMirror stringTypeMirror;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
        types = processingEnvironment.getTypeUtils();
        extraKeys = new HashSet<>();
        classWithFields = new HashMap<>();
        classWithSaveFields = new HashMap<>();
        classWithExtras = new HashMap<>();
        classWithSaveExtras = new HashMap<>();
        classWithParents = new HashMap<>();
        requiredClassWithParents = new HashMap<>();
        requiredClassWithFields = new HashMap<>();

        fragmentTypeMirror = types
                .erasure(elements.getTypeElement("android.app.Fragment").asType());
        fragmentV4TypeMirror = types
                .erasure(elements.getTypeElement("android.support.v4.app.Fragment").asType());
        parcelableTypeMirror = types
                .erasure(elements.getTypeElement("android.os.Parcelable").asType());
        serializableTypeMirror = types
                .erasure(elements.getTypeElement("java.io.Serializable").asType());
        listTypeMirror = types
                .erasure(elements.getTypeElement("java.util.List").asType());
        integerTypeMirror = types
                .erasure(elements.getTypeElement("java.lang.Integer").asType());
        stringTypeMirror = types
                .erasure(elements.getTypeElement("java.lang.String").asType());
        charSequenceTypeMirror = types
                .erasure(elements.getTypeElement("java.lang.CharSequence").asType());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        try {
            mapAllAnnotatedElement(roundEnvironment);
            createRouterConfigClass();
            createRouterClass();
            createExtrasBindingClass();
        } catch (Exception e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, e.getMessage());
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Extra.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void mapAllAnnotatedElement(RoundEnvironment roundEnvironment) throws Exception {
        /*
         * 1- Find Route annotations
         */
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Route.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                throw new Exception("Can be applied to class.");
            }

            /*
             * 1.1- Map class with parents
             */
            TypeElement typeElement = (TypeElement) element;
            TypeElement parentTypeElement = (TypeElement) ((DeclaredType) typeElement.getSuperclass()).asElement();

            if (!classWithParents.containsKey(typeElement)) {
                classWithParents.put(typeElement, parentTypeElement);
            }
        }

        /*
         * 2- Find Extra annotations
         */
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Extra.class)) {
            if (element.getKind() != ElementKind.FIELD) {
                throw new Exception("Can be applied to field.");
            }

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            TypeMirror typeMirror = element.asType();
            Extra extra = element.getAnnotation(Extra.class);

            extraKeys.add(extra.key());

            /*
             * 2.1- Map class with field
             */
            Map<Element, TypeMirror> fields = classWithFields.get(typeElement);
            if (fields == null) {
                fields = new HashMap<>();
            }
            fields.put(element, typeMirror);
            classWithFields.put(typeElement, fields);

            /*
             * 2.2- Map class with extra annotation
             */
            Map<Element, Extra> extras = classWithExtras.get(typeElement);
            if (extras == null) {
                extras = new HashMap<>();
            }
            extras.put(element, extra);
            classWithExtras.put(typeElement, extras);

            /*
             * 2.3- Map required element
             */
            if (extra.required()) {
                if (!requiredClassWithParents.containsKey(typeElement)) {
                    requiredClassWithParents.put(typeElement, classWithParents.get(typeElement));
                }

                Map<Element, TypeMirror> requiredFields = requiredClassWithFields.get(typeElement);
                if (requiredFields == null) {
                    requiredFields = new HashMap<>();
                }
                requiredFields.put(element, typeMirror);
                requiredClassWithFields.put(typeElement, requiredFields);
            }
        }

        /*
         * 2- Find SaveExtra annotations
         */
        for (Element element : roundEnvironment.getElementsAnnotatedWith(SaveExtra.class)) {
            if (element.getKind() != ElementKind.FIELD) {
                throw new Exception("Can be applied to field.");
            }

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            TypeMirror typeMirror = element.asType();
            SaveExtra extra = element.getAnnotation(SaveExtra.class);

            extraKeys.add(extra.key());

            /*
             * 2.1- Map class with field
             */
            Map<Element, TypeMirror> fields = classWithSaveFields.get(typeElement);
            if (fields == null) {
                fields = new HashMap<>();
            }
            fields.put(element, typeMirror);
            classWithSaveFields.put(typeElement, fields);

            /*
             * 2.2- Map class with extra annotation
             */
            Map<Element, SaveExtra> extras = classWithSaveExtras.get(typeElement);
            if (extras == null) {
                extras = new HashMap<>();
            }
            extras.put(element, extra);
            classWithSaveExtras.put(typeElement, extras);
        }

        /*
         * 3- Finalize required mapping
         */
        for (Map.Entry<TypeElement, TypeElement> entry : classWithParents.entrySet()) {
            TypeElement typeElement = entry.getKey();
            TypeElement parentTypeElement = entry.getValue();

            checkParentTypeElementHasRequired(typeElement, parentTypeElement);
        }
    }

    private boolean checkParentTypeElementHasRequired(TypeElement typeElement, TypeElement parentTypeElement) {
        if (!classWithParents.containsKey(typeElement)) {
            return false;
        }

        if (requiredClassWithParents.containsKey(typeElement)) {
            return true;
        }

        DeclaredType declaredType = (DeclaredType) parentTypeElement.getSuperclass();
        TypeElement parentParentTypeElement = (TypeElement) declaredType.asElement();

        if (checkParentTypeElementHasRequired(parentTypeElement, parentParentTypeElement)) {
            requiredClassWithParents.put(parentTypeElement, parentParentTypeElement);
            return true;
        }

        return false;
    }

    private void createRouterConfigClass() throws IOException {
        /*
         * 1- Create RouterConfig class builder
         */
        TypeSpec.Builder routerConfigClassBuilder = TypeSpec
                .classBuilder(typeClassRouterConfig)
                .addModifiers(Modifier.PUBLIC);

        /*
         * 2- Add extra keys
         */
        for (String key : extraKeys) {
            FieldSpec.Builder fieldSpecBuilder = FieldSpec
                    .builder(String.class, ROUTER_CONFIG_EXTRAS_PREFIX + key.toUpperCase(), Modifier.FINAL,
                             Modifier.STATIC, Modifier.PUBLIC);
            fieldSpecBuilder = fieldSpecBuilder.initializer("\"" + key + "\"");
            routerConfigClassBuilder.addField(fieldSpecBuilder.build());
        }

        /*
         * 3- Write generated class to a file
         */
        JavaFile.builder(typeClassRouterConfig.packageName(), routerConfigClassBuilder.build()).build().writeTo(filer);
    }

    private void createRouterClass() throws IOException {
        for (Map.Entry<TypeElement, TypeElement> clazz : classWithParents.entrySet()) {
            /*
             * 1- Get data
             */
            TypeElement typeElement = clazz.getKey();
            TypeElement parentTypeElement = clazz.getValue();

            String className = typeElement.getSimpleName().toString();
            String classPackageName = elements.getPackageOf(typeElement).getQualifiedName().toString();

            /*
             * 2- Check type element is activity or fragment or fragment v4
             */
            boolean isFragment = false;
            boolean isFragmentV4 = false;
            if (types.isAssignable(typeElement.asType(), fragmentTypeMirror) || types
                    .isAssignable(typeElement.asType(), fragmentV4TypeMirror)) {
                isFragment = true;
                isFragmentV4 = types.isAssignable(typeElement.asType(), fragmentV4TypeMirror);
            }

            /*
             * 3- Get parent type element
             */
            ClassName routerSuperClassName = typeClassBaseRouter;

            if (isFragment) {
                routerSuperClassName = typeClassBaseFragmentRouter;
            }

            if (classWithParents.containsKey(parentTypeElement)) {
                String parentSimpleName = parentTypeElement.getSimpleName().toString();
                String parentPackageName = elements.getPackageOf(parentTypeElement).getQualifiedName().toString();
                routerSuperClassName = ClassName.get(parentPackageName, parentSimpleName + ROUTER_CLASS_SUFFIX);
            }

            /*
             * 4- Create router class builder
             */
            TypeSpec.Builder routerClassBuilder = TypeSpec
                    .classBuilder(className + ROUTER_CLASS_SUFFIX)
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(routerSuperClassName);

            /*
             * 5- Create Method put if has fields
             */
            Map<Element, TypeMirror> fields = classWithFields.get(typeElement);
            if (fields != null) {
                ClassName returnMethodClassName = ClassName.get(classPackageName, className + ROUTER_CLASS_SUFFIX);

                for (Map.Entry<Element, TypeMirror> field : fields.entrySet()) {
                    Element element = field.getKey();
                    TypeMirror typeMirror = field.getValue();
                    Extra extra = classWithExtras.get(typeElement).get(element);

                    if (extra.required()) {
                        continue;
                    }

                    String elementName = element.getSimpleName().toString();
                    String methodName = ROUTER_METHOD_PREFIX + elementName.substring(0, 1).toUpperCase() + elementName
                            .substring(1);

                    TypeName typeNameParameter = getTypeNameParameter(typeMirror);
                    String statement = String.format(bundlePutStatement, extra.key(), elementName);

                    if (types.isAssignable(typeMirror, listTypeMirror)) {
                        String stringTypeName = ClassName.get(typeMirror).toString();
                        int idx1 = stringTypeName.indexOf("<") + 1;
                        int idx2 = stringTypeName.indexOf(">");
                        String stringGenericTypeName = stringTypeName.substring(idx1, idx2).replace("? extends ", "");
                        TypeMirror genericTypeMirror = elements.getTypeElement(stringGenericTypeName).asType();
                        if (types.isAssignable(genericTypeMirror, integerTypeMirror)) {
                            statement = String
                                    .format(bundlePutIntegerArrayListStatement, extra.key(), stringGenericTypeName,
                                            elementName);
                        }
                        else if (types.isAssignable(genericTypeMirror, stringTypeMirror)) {
                            statement = String
                                    .format(bundlePutStringArrayListStatement, extra.key(), stringGenericTypeName,
                                            elementName);
                        }
                        else if (types.isAssignable(genericTypeMirror, charSequenceTypeMirror)) {
                            statement = String
                                    .format(bundlePutCharSequenceArrayListStatement, extra.key(), stringGenericTypeName,
                                            elementName);
                        }
                        else {
                            typeNameParameter = ClassName.get(listTypeMirror);
                            statement = String
                                    .format(bundlePutParcelableArrayListStatement, extra.key(), "android.os.Parcelable",
                                            elementName);
                        }
                    }

                    MethodSpec.Builder methodBuilder = MethodSpec
                            .methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(returnMethodClassName)
                            .addParameter(typeNameParameter, elementName)
                            .addStatement(statement)
                            .addStatement("return this");

                    routerClassBuilder.addMethod(methodBuilder.build());
                }
            }

            /*
             * 6- Create Constructor
             */
            ClassName returnStaticNewInstanceClassName = ClassName
                    .get(classPackageName, className + ROUTER_CLASS_SUFFIX);
            TypeElement requiredParentTypeElement = parentTypeElement;
            MethodSpec.Builder routerConstructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
            MethodSpec.Builder routerStaticNewInstanceBuilder = MethodSpec.methodBuilder("newInstance")
                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC).returns(returnStaticNewInstanceClassName);
            String superStatement = "super(";
            String newInstanceStatement = "return new " + className + ROUTER_CLASS_SUFFIX + "(";

            while (requiredClassWithParents.containsKey(requiredParentTypeElement)) {
                Map<Element, TypeMirror> requiredParentFields = requiredClassWithFields.get(parentTypeElement);
                if (requiredParentFields != null && !requiredParentFields.isEmpty()) {
                    TreeMap<Element, TypeMirror> treeMap = new TreeMap<>(
                            Comparator.comparing(e -> e.getSimpleName().toString()));
                    treeMap.putAll(requiredParentFields);
                    for (Map.Entry<Element, TypeMirror> entry : treeMap.entrySet()) {
                        Element requiredElement = entry.getKey();
                        TypeMirror requiredTypeMirror = entry.getValue();
                        String name = requiredElement.getSimpleName().toString();

                        TypeName requiredTypeNameParameter = getTypeNameParameter(requiredTypeMirror);

                        routerConstructorBuilder.addParameter(requiredTypeNameParameter, name);
                        routerStaticNewInstanceBuilder.addParameter(requiredTypeNameParameter, name);

                        superStatement = superStatement.concat(name).concat(", ");
                        newInstanceStatement = newInstanceStatement.concat(name).concat(", ");
                    }
                }

                requiredParentTypeElement = requiredClassWithParents.get(requiredParentTypeElement);
            }

            if (superStatement.charAt(superStatement.length() - 1) != '(') {
                superStatement = superStatement.substring(0, superStatement.length() - 2);
            }
            superStatement = superStatement.concat(")");
            routerConstructorBuilder.addStatement(superStatement);

            Map<Element, TypeMirror> fields1 = requiredClassWithFields.get(typeElement);
            if (fields1 != null) {
                TreeMap<Element, TypeMirror> treeMap = new TreeMap<>(
                        Comparator.comparing(e -> e.getSimpleName().toString()));
                treeMap.putAll(fields1);
                for (Map.Entry<Element, TypeMirror> entry : treeMap.entrySet()) {
                    Element requiredElement = entry.getKey();
                    TypeMirror requiredTypeMirror = entry.getValue();
                    String name = requiredElement.getSimpleName().toString();
                    Extra extra = classWithExtras.get(typeElement).get(requiredElement);

                    TypeName requiredTypeNameParameter = getTypeNameParameter(requiredTypeMirror);
                    String statement = String.format(bundlePutStatement, extra.key(), name);

                    if (types.isAssignable(requiredTypeMirror, listTypeMirror)) {
                        String stringTypeName = ClassName.get(requiredTypeMirror).toString();
                        int idx1 = stringTypeName.indexOf("<") + 1;
                        int idx2 = stringTypeName.indexOf(">");
                        String stringGenericTypeName = stringTypeName.substring(idx1, idx2).replace("? extends ", "");
                        TypeMirror genericTypeMirror = elements.getTypeElement(stringGenericTypeName).asType();
                        if (types.isAssignable(genericTypeMirror, integerTypeMirror)) {
                            statement = String
                                    .format(bundlePutIntegerArrayListStatement, extra.key(), stringGenericTypeName,
                                            name);
                        }
                        else if (types.isAssignable(genericTypeMirror, stringTypeMirror)) {
                            statement = String
                                    .format(bundlePutStringArrayListStatement, extra.key(), stringGenericTypeName,
                                            name);
                        }
                        else if (types.isAssignable(genericTypeMirror, charSequenceTypeMirror)) {
                            statement = String
                                    .format(bundlePutCharSequenceArrayListStatement, extra.key(), stringGenericTypeName,
                                            name);
                        }
                        else {
                            requiredTypeNameParameter = ClassName.get(listTypeMirror);
                            statement = String
                                    .format(bundlePutParcelableArrayListStatement, extra.key(), "android.os.Parcelable",
                                            name);
                        }
                    }

                    routerConstructorBuilder.addParameter(requiredTypeNameParameter, name)
                            .addStatement(statement);
                    routerStaticNewInstanceBuilder.addParameter(requiredTypeNameParameter, name);
                    newInstanceStatement = newInstanceStatement.concat(name).concat(", ");
                }
            }

            if (newInstanceStatement.charAt(newInstanceStatement.length() - 1) != '(') {
                newInstanceStatement = newInstanceStatement.substring(0, newInstanceStatement.length() - 2);
            }
            newInstanceStatement = newInstanceStatement.concat(")");
            routerStaticNewInstanceBuilder.addStatement(newInstanceStatement);

            routerClassBuilder.addMethod(routerConstructorBuilder.build());
            routerClassBuilder.addMethod(routerStaticNewInstanceBuilder.build());

            /*
             * 7- Create Method create intent
             */
            MethodSpec.Builder methodBuilder = MethodSpec
                    .methodBuilder("create")
                    .addModifiers(Modifier.PUBLIC);

            if (isFragment) {
                methodBuilder.returns(ClassName.get(typeElement));
                if (isFragmentV4) {
                    methodBuilder.addStatement("return super.createV4(" + className + ".class)");
                }
                else {
                    methodBuilder.addStatement("return super.create(" + className + ".class)");
                }
            }
            else {
                methodBuilder.returns(typeClassIntent)
                        .addParameter(typeClassContext, "context")
                        .addStatement("return super.create(context, " + className + ".class)");
            }

            routerClassBuilder.addMethod(methodBuilder.build());

            /*
             * 8- Write generated class to a file
             */
            JavaFile.builder(classPackageName, routerClassBuilder.build()).build().writeTo(filer);
        }
    }

    private TypeName getTypeNameParameter(TypeMirror typeMirror) {
        TypeName typeNameParameter = ClassName.get(typeMirror);
        if (types.isAssignable(typeMirror, listTypeMirror)) {
            return typeNameParameter;
        }

        if (!(typeNameParameter.isPrimitive() || typeNameParameter
                .isBoxedPrimitive() || types
                .isAssignable(typeMirror, parcelableTypeMirror) || types
                .isAssignable(typeMirror, serializableTypeMirror))) {
            typeNameParameter = typeClassParcelable;
        }
        return typeNameParameter;
    }

    private void createExtrasBindingClass() throws IOException {
        for (Map.Entry<TypeElement, TypeElement> enclosingElement : classWithParents.entrySet()) {
            TypeElement enclosingTypeElement = enclosingElement.getKey();
            Map<Element, Extra> enclosingExtras = classWithExtras.get(enclosingTypeElement);
            Map<Element, SaveExtra> enclosingSaveExtras = classWithSaveExtras.get(enclosingTypeElement);

            createExtrasBindingClass(enclosingTypeElement, enclosingExtras, enclosingSaveExtras);
        }

        for (Map.Entry<TypeElement, Map<Element, Extra>> enclosingElement : classWithExtras.entrySet()) {
            TypeElement enclosingTypeElement = enclosingElement.getKey();
            Map<Element, Extra> enclosingExtras = enclosingElement.getValue();
            Map<Element, SaveExtra> enclosingSaveExtras = classWithSaveExtras.get(enclosingTypeElement);

            if (classWithParents.containsKey(enclosingTypeElement)) {
                continue;
            }

            createExtrasBindingClass(enclosingTypeElement, enclosingExtras, enclosingSaveExtras);
        }
    }

    private void createExtrasBindingClass(TypeElement enclosingTypeElement, Map<Element, Extra> enclosingExtras, Map<Element, SaveExtra> enclosingSaveExtras)
            throws IOException {
        DeclaredType parentEnclosingDeclareType = (DeclaredType) enclosingTypeElement.getSuperclass();
        String enclosingSimpleName = enclosingTypeElement.getSimpleName().toString();
        String enclosingPackageName = elements.getPackageOf(enclosingTypeElement).getQualifiedName().toString();

        ClassName binderSuperClassName = typeClassExtrasBinding;
        if (classWithParents.containsKey(parentEnclosingDeclareType.asElement())) {
            TypeElement parentTypeElement = (TypeElement) parentEnclosingDeclareType.asElement();
            String parentSimpleName = parentTypeElement.getSimpleName().toString();
            String parentPackageName = elements.getPackageOf(parentTypeElement).getQualifiedName().toString();
            binderSuperClassName = ClassName.get(parentPackageName, parentSimpleName + BINDER_CLASS_SUFFIX);
        }

        TypeSpec.Builder binderClassBuilder = TypeSpec
                .classBuilder(enclosingSimpleName + BINDER_CLASS_SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .superclass(binderSuperClassName);

        MethodSpec.Builder binderConstructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(enclosingTypeElement), "target")
                .addParameter(typeClassBundle, "bundle")
                .addParameter(ArrayTypeName.of(typeClassBundle), "optionals")
                .varargs(true);
        MethodSpec.Builder onSaveInstanceStateMethodSpecBuilder = MethodSpec.methodBuilder("onSaveInstanceState")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(enclosingTypeElement), "target")
                .addParameter(typeClassBundle, "outState");

        if (binderSuperClassName == typeClassExtrasBinding) {
            binderConstructorBuilder.addStatement("super(bundle, optionals)");
        }
        else {
            binderConstructorBuilder.addStatement("super(target, bundle, optionals)");
        }

        /*
         * 1- Create Method put
         */
        Map<Element, TypeMirror> fields = classWithFields.get(enclosingTypeElement);
        if (fields != null) {
            for (Map.Entry<Element, TypeMirror> entry : fields.entrySet()) {
                Element element = entry.getKey();
                TypeMirror typeMirror = entry.getValue();
                TypeName typeName = ClassName.get(typeMirror);
                String name = element.getSimpleName().toString();
                Extra extra = enclosingExtras.get(element);
                String stringTypeName = typeName.toString();

                if (types.isAssignable(typeMirror, listTypeMirror)) {
                    int idx1 = stringTypeName.indexOf("<");
                    stringTypeName = stringTypeName.substring(0, idx1);
                }

                String statement = String
                        .format(bundleGetStatement, extra.key(), "target." + name, stringTypeName + ".class");

                binderConstructorBuilder.addCode("try {");
                binderConstructorBuilder.addStatement("target." + name + " = (" + typeName + ") " + statement);
                binderConstructorBuilder.addCode("} catch (Exception e) {");
                binderConstructorBuilder.addStatement("e.printStackTrace()");
                binderConstructorBuilder.addCode("}");
            }
        }

        /*
         * 2- Create Binder constructor
         */
        binderClassBuilder.addMethod(binderConstructorBuilder.build());

        /*
         * 3- Create Save instance
         */
        fields = classWithSaveFields.get(enclosingTypeElement);
        if (fields != null) {
            if (binderSuperClassName == typeClassExtrasBinding) {
                onSaveInstanceStateMethodSpecBuilder.addStatement("super.onSaveInstanceState(outState)");
            }
            else {
                onSaveInstanceStateMethodSpecBuilder.addStatement("super.onSaveInstanceState(target, outState)");
            }
            onSaveInstanceStateMethodSpecBuilder.addCode("try {");
            onSaveInstanceStateMethodSpecBuilder.addStatement("android.content.Intent intent = new android.content.Intent()");
            for (Map.Entry<Element, TypeMirror> entry : fields.entrySet()) {
                Element element = entry.getKey();
                TypeMirror typeMirror = entry.getValue();
                TypeName typeName = ClassName.get(typeMirror);
                String name = element.getSimpleName().toString();
                SaveExtra extra = enclosingSaveExtras.get(element);
                String stringTypeName = typeName.toString();

                if (types.isAssignable(typeMirror, listTypeMirror)) {
                    int idx1 = stringTypeName.indexOf("<");
                    stringTypeName = stringTypeName.substring(0, idx1);
                }

                String statement = String.format(bundlePutStatement, extra.key(), "target." + name);
                TypeName typeNameParameter = getTypeNameParameter(typeMirror);
                if (types.isAssignable(typeMirror, listTypeMirror)) {
                    stringTypeName = ClassName.get(typeMirror).toString();
                    int idx1 = stringTypeName.indexOf("<") + 1;
                    int idx2 = stringTypeName.indexOf(">");
                    String stringGenericTypeName = stringTypeName.substring(idx1, idx2).replace("? extends ", "");
                    TypeMirror genericTypeMirror = elements.getTypeElement(stringGenericTypeName).asType();
                    if (types.isAssignable(genericTypeMirror, integerTypeMirror)) {
                        statement = String
                                .format(bundlePutIntegerArrayListStatement, extra.key(), stringGenericTypeName,
                                        "target." + name);
                    }
                    else if (types.isAssignable(genericTypeMirror, stringTypeMirror)) {
                        statement = String
                                .format(bundlePutStringArrayListStatement, extra.key(), stringGenericTypeName,
                                        "target." + name);
                    }
                    else if (types.isAssignable(genericTypeMirror, charSequenceTypeMirror)) {
                        statement = String
                                .format(bundlePutCharSequenceArrayListStatement, extra.key(), stringGenericTypeName,
                                        "target." + name);
                    }
                    else {
                        statement = String
                                .format(bundlePutParcelableArrayListStatement, extra.key(), "android.os.Parcelable",
                                        "target." + name);
                    }
                }
                else if (typeNameParameter.equals(typeClassParcelable)) {
                    statement = "intent.putExtra(\"" + extra.key() + "\", (android.os.Parcelable) getTypeConverterExtraValue(target." + name + ", " + stringTypeName + ".class))";
                }

                onSaveInstanceStateMethodSpecBuilder.addStatement(statement);
            }
            onSaveInstanceStateMethodSpecBuilder.addStatement("outState.putAll(intent.getExtras())");
            onSaveInstanceStateMethodSpecBuilder.addCode("} catch (Exception e) {");
            onSaveInstanceStateMethodSpecBuilder.addStatement("e.printStackTrace()");
            onSaveInstanceStateMethodSpecBuilder.addCode("}");
            binderClassBuilder.addMethod(onSaveInstanceStateMethodSpecBuilder.build());
        }

        /*
         * 4- Write generated class to a file
         */
        JavaFile.builder(enclosingPackageName, binderClassBuilder.build()).build().writeTo(filer);
    }
}
