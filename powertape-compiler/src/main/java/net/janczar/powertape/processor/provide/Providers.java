package net.janczar.powertape.processor.provide;


import com.squareup.javapoet.JavaFile;

import net.janczar.powertape.annotation.Scope;
import net.janczar.powertape.log.Log;
import net.janczar.powertape.processor.TypeUtil;
import net.janczar.powertape.processor.codegen.ProviderCodeGen;
import net.janczar.powertape.annotation.Singleton;
import net.janczar.powertape.processor.inject.Injectors;
import net.janczar.powertape.processor.resolve.ResolverOld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class Providers {

    private final Map<String, List<Provider>> providers = new HashMap<>();

    private final List<Provider> all = new ArrayList<>();

    public void clear() {
        providers.clear();
    }

    public void process(final List<ExecutableElement> constructors) {
        for (ExecutableElement constructor : constructors) {
            process(constructor);
        }
    }

    public void process(final ExecutableElement constructor) {
        TypeElement classElement = (TypeElement)constructor.getEnclosingElement();

        String instanceClassName = classElement.getQualifiedName().toString();

        List<? extends VariableElement> parameters = constructor.getParameters();
        ProviderDependency[] dependencies = new ProviderDependency[parameters.size()];
        for (int i=0; i<parameters.size(); i++) {
            VariableElement parameter = parameters.get(i);
            String parameterName = parameter.getSimpleName().toString();

            if (parameter.asType().getKind() != TypeKind.DECLARED) {
                Log.error(String.format("@Provide constructor parameter %s must not be of simple injectedType!", parameterName), constructor);
                continue;
            }

            dependencies[i] = new ProviderDependency(parameterName, (DeclaredType)parameter.asType());
        }

        Singleton singletonAnnotation = constructor.getAnnotation(Singleton.class);
        Scope scopeAnnotation = constructor.getAnnotation(Scope.class);

        if (singletonAnnotation != null && scopeAnnotation != null) {
            Log.error("Singleton and Scope annotations can't be used at the same time!", constructor);
            return;
        }

        ProviderScope providerScope = null;
        if (singletonAnnotation != null) {
            providerScope = new ProviderScope(ProviderScope.Type.SINGLETON);
        } else if (scopeAnnotation != null) {
            providerScope = new ProviderScope(ProviderScope.Type.TYPE, TypeUtil.getScopeClass(constructor));
        } else {
            providerScope = new ProviderScope(ProviderScope.Type.DEFAULT);
        }

        for (TypeMirror implementedInterface : classElement.getInterfaces()) {
            String interfaceName = TypeUtil.getQualifiedName(implementedInterface);
            addProvider(new ConstructorProvider(constructor, providerScope, (DeclaredType)implementedInterface, (DeclaredType)classElement.asType(), dependencies));
        }

        addProvider(new ConstructorProvider(constructor, providerScope, (DeclaredType)classElement.asType(), (DeclaredType)classElement.asType(), dependencies));
    }

    public void generateCode(Filer filer) {
        for (List<Provider> list : providers.values()) {
            JavaFile javaFile = ProviderCodeGen.generateCode(list.get(0));
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                Log.error("Could not write provider class: "+e.getMessage(), list.get(0).element);
            }
        }
    }

    public Provider getProvider(final String providedClassName) {
        List<Provider> list = providers.get(providedClassName);
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public List<Provider> all() {
        return all;
    }

    public void resolve(final Injectors injectors) {
        for (Provider provider : all) {
            ResolverOld.verifyScope(this, injectors, provider);
        }
    }

    private void addProvider(final Provider provider) {
        String providedClassName = ((TypeElement)provider.providedClass.asElement()).getQualifiedName().toString();
        List<Provider> providersList = providers.get(providedClassName);
        if (providersList == null) {
            providersList = new ArrayList<>();
            providers.put(providedClassName, providersList);
        }
        providersList.add(provider);
        all.add(provider);
    }
}
