package net.janczar.powertape.processor.inject;


import com.squareup.javapoet.JavaFile;

import net.janczar.powertape.log.Log;
import net.janczar.powertape.processor.TypeUtil;
import net.janczar.powertape.processor.codegen.InjectorCodeGen;
import net.janczar.powertape.processor.provide.Providers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;

public class Injectors {

    private final Map<String, Injector> injectors = new HashMap<>();

    public void clear() {
        injectors.clear();
    }

    public void process(final List<VariableElement> injectedFields) {
        for (VariableElement field : injectedFields) {
            createInjector((DeclaredType)field.getEnclosingElement().asType()).addInjectedField(field);
        }
    }

    public void resolve(final Elements elements, final Providers providers) {
        for (Injector injector : injectors.values()) {
            injector.resolve(elements, providers);
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

    public Injector getInjector(final DeclaredType injectedType) {
        String injectedClassName = TypeUtil.getQualifiedName(injectedType);
        return injectors.get(injectedClassName);
    }

    public Collection<Injector> all() {
        return injectors.values();
    }

    private Injector createInjector(final DeclaredType injectedType) {
        String injectedClassName = TypeUtil.getQualifiedName(injectedType);
        Injector injector = injectors.get(injectedClassName);
        if (injector == null) {
            injector = new Injector(injectedType);
            injectors.put(injectedClassName, injector);
        }
        return injector;
    }
}
