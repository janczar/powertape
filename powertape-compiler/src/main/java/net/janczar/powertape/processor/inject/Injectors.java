package net.janczar.powertape.processor.inject;


import com.squareup.javapoet.JavaFile;

import net.janczar.powertape.processor.Log;
import net.janczar.powertape.processor.codegen.InjectorCodeGen;
import net.janczar.powertape.processor.provide.Providers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;

public class Injectors {

    private final Map<String, Injector> injectors = new HashMap<>();

    public void clear() {
        injectors.clear();
    }

    public void process(final Collection<? extends Element> injectElements) {
        List<VariableElement> injectedFields = ElementFilter.fieldsIn(injectElements);
        for (VariableElement field : injectedFields) {
            getInjector((DeclaredType)field.getEnclosingElement().asType()).addInjectedField(field);
        }
    }

    public void resolve(final Providers providers) {
        for (Injector injector : injectors.values()) {
            injector.resolve(providers);
        }
    }

    public void generateCode(Filer filer) {
        for (Injector injector : injectors.values()) {
            JavaFile javaFile = InjectorCodeGen.generateCode(injector);
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                Log.error("Could not write injector class: "+e.getMessage());
            }
        }
    }

    private Injector getInjector(final DeclaredType injectedType) {
        String injectedClassName = ((TypeElement)injectedType.asElement()).getQualifiedName().toString();
        Injector injector = injectors.get(injectedClassName);
        if (injector == null) {
            injector = new Injector(injectedType);
            injectors.put(injectedClassName, injector);
        }
        return injector;
    }
}
