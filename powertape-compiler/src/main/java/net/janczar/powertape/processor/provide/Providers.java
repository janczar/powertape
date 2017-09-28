package net.janczar.powertape.processor.provide;


import com.squareup.javapoet.JavaFile;

import net.janczar.powertape.processor.Log;
import net.janczar.powertape.processor.TypeUtil;
import net.janczar.powertape.processor.codegen.ProviderCodeGen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

public class Providers {

    private final Map<String, List<Provider>> providers = new HashMap<>();

    public void clear() {
        providers.clear();
    }

    public void process(final Collection<? extends Element> provideElements) {
        for (ExecutableElement constructor : ElementFilter.constructorsIn(provideElements)) {
            TypeElement classElement = (TypeElement)constructor.getEnclosingElement();

            String instanceClassName = classElement.getQualifiedName().toString();

            List<? extends VariableElement> parameters = constructor.getParameters();
            ProviderDependency[] dependencies = new ProviderDependency[parameters.size()];
            for (int i=0; i<parameters.size(); i++) {
                VariableElement parameter = parameters.get(i);
                String parameterName = parameter.getSimpleName().toString();

                if (parameter.asType().getKind() != TypeKind.DECLARED) {
                    Log.error(String.format("@Provide constructor parameter %s must not be of simple type!", parameterName), constructor);
                    continue;
                }

                String parameterClassName = TypeUtil.getQualifiedName(parameter.asType());

                dependencies[i] = new ProviderDependency(parameterName, (DeclaredType)parameter.asType());
            }

            for (TypeMirror implementedInterface : classElement.getInterfaces()) {
                String interfaceName = TypeUtil.getQualifiedName(implementedInterface);
                addProvider(new ConstructorProvider(constructor, (DeclaredType)implementedInterface, instanceClassName, dependencies));
            }

            addProvider(new ConstructorProvider(constructor, (DeclaredType)classElement.asType(), instanceClassName, dependencies));
        }
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

    private void addProvider(final Provider provider) {
        String providedClassName = ((TypeElement)provider.providedClass.asElement()).getQualifiedName().toString();
        List<Provider> providersList = providers.get(providedClassName);
        if (providersList == null) {
            providersList = new ArrayList<>();
            providers.put(providedClassName, providersList);
        }
        providersList.add(provider);
    }
}
