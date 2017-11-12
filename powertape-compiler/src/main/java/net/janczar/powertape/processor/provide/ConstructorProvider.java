package net.janczar.powertape.processor.provide;


import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;

public class ConstructorProvider extends Provider {

    public final DeclaredType instanceClass;

    public ConstructorProvider(final ExecutableElement constructor, final ProviderScope providerScope, final DeclaredType providedClass, final DeclaredType instanceClass, final ProviderDependency[] constructorArguments) {
        super(constructor, ProviderType.CONSTRUCTOR, providerScope, providedClass, constructorArguments);
        this.instanceClass = instanceClass;
    }

}
