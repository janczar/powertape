package net.janczar.powertape.example.test;


import net.janczar.powertape.annotation.Inject;

public class TestTargetB {

    @Inject
    public DefaultScopeTest defaultScopeA;

    @Inject
    public DefaultScopeTest defaultScopeB;

    @Inject
    public SingletonScopeTest singletonScopeA;

    @Inject
    public SingletonScopeTest singletonScopeB;

}
