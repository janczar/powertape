package net.janczar.powertape.processor.provide;


import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;

public class ConstructorProvider extends Provider {

    public final String instanceClassName;

    public ConstructorProvider(final ExecutableElement constructor, final Scope scope, final DeclaredType providedClass, final String instanceClassName, final ProviderDependency[] constructorArguments) {
        super(constructor, ProviderType.CONSTRUCTOR, scope, providedClass, constructorArguments);
        this.instanceClassName = instanceClassName;
    }

}
