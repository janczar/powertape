package net.janczar.powertape.processor.provide;


import javax.lang.model.type.DeclaredType;

public class ProviderDependency {

    public final String name;

    public final DeclaredType dependencyClass;

    public ProviderDependency(final String name, final DeclaredType dependencyClass) {
        this.name = name;
        this.dependencyClass = dependencyClass;
    }

}
