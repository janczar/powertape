package net.janczar.powertape.processor.provide;


import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class Provider {

    public final Element element;

    public final ProviderType type;

    public final DeclaredType providedClass;

    public final ProviderDependency[] dependencies;

    public final Scope scope;

    public boolean hasInjectedFields;

    public Provider(final Element element, final ProviderType type, final Scope scope, final DeclaredType providedClass) {
        this(element, type, scope, providedClass, new ProviderDependency[0]);
    }

    public Provider(final Element element, final ProviderType type, final Scope scope, final DeclaredType providedClass, final ProviderDependency[] dependencies) {
        this.element = element;
        this.type = type;
        this.scope = scope;
        this.providedClass = providedClass;
        this.dependencies = dependencies;
    }
}
