package net.janczar.powertape.example.test;


import net.janczar.powertape.annotation.Inject;
import net.janczar.powertape.annotation.Provide;
import net.janczar.powertape.annotation.Singleton;

public class SingletonScopeTest {

    @Inject
    public SingletonDependencyTest dependency;

    @Provide
    @Singleton
    public SingletonScopeTest() {

    }

}
