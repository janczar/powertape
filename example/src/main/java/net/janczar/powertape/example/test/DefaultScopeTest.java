package net.janczar.powertape.example.test;


import net.janczar.powertape.annotation.Inject;
import net.janczar.powertape.annotation.Provide;

public class DefaultScopeTest {

    @Inject
    InjectedDependencyTest injectedDependency;

    @Provide
    public DefaultScopeTest(final ConstructorParamTest constructorParam) {
    }

}
