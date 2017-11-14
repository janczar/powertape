package net.janczar.powertape.processor.inject;


import net.janczar.powertape.log.Log;
import net.janczar.powertape.processor.provide.Provider;
import net.janczar.powertape.processor.provide.Providers;
import net.janczar.powertape.processor.resolve.Resolver;
import net.janczar.powertape.processor.TypeUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;

public class Injector {

    public final DeclaredType injectedClass;

    public final List<InjectedField> injectedFields = new ArrayList<>();

    public Injector(final DeclaredType injectedClass) {
        this.injectedClass = injectedClass;
    }

    public void addInjectedField(final VariableElement field) {
        if (field.asType().getKind() != TypeKind.DECLARED) {
            Log.error(String.format("Field %s cannot be injected because it is of simple type!", field.getSimpleName()), field.getEnclosingElement());
            return;
        }
        if (field.getModifiers().contains(Modifier.PRIVATE)) {
            Log.error(String.format("Field %s cannot be injected because it is private!", field.getSimpleName()), field.getEnclosingElement());
            return;
        }
        if (field.getModifiers().contains(Modifier.FINAL)) {
            Log.error(String.format("Field %s cannot be injected because it is final!", field.getSimpleName()), field.getEnclosingElement());
            return;
        }

        injectedFields.add(new InjectedField((TypeElement)field.getEnclosingElement(), field.getSimpleName().toString(), (DeclaredType)field.asType(), TypeUtil.getQualifiedName(field.asType())));
    }

    public void resolve(final Elements elements, final Providers providers) {
        Provider provider = providers.getProvider(TypeUtil.getQualifiedName(injectedClass));
        if (provider != null) {
            provider.hasInjectedFields = true;
        }

        for (Iterator<InjectedField> it = injectedFields.iterator(); it.hasNext();) {
            if (!Resolver.resolveField(elements, providers, it.next())) {
                it.remove();
            }
        }
    }

}
