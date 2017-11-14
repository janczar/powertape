package net.janczar.powertape.example.test;


import net.janczar.powertape.annotation.Provide;
import net.janczar.powertape.annotation.Scope;

public class SingletonDependencyTest {

    @Provide
    @Scope(SingletonScopeTest.class)
    public SingletonDependencyTest() {

    }

}
