package net.janczar.powertape.processor.provide;


import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;

public class Provider {

    public final Element element;

    public final ProviderType type;

    public final DeclaredType providedClass;

    public final ProviderDependency[] dependencies;

    public final ProviderScope providerScope;

    public boolean hasInjectedFields;

    public Provider(final Element element, final ProviderType type, final ProviderScope providerScope, final DeclaredType providedClass) {
        this(element, type, providerScope, providedClass, new ProviderDependency[0]);
    }

    public Provider(final Element element, final ProviderType type, final ProviderScope providerScope, final DeclaredType providedClass, final ProviderDependency[] dependencies) {
        this.element = element;
        this.type = type;
        this.providerScope = providerScope;
        this.providedClass = providedClass;
        this.dependencies = dependencies;
    }
}
