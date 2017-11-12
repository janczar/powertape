package net.janczar.powertape;


import net.janczar.powertape.example.test.TestTargetA;
import net.janczar.powertape.example.test.TestTargetB;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ScopesTest {

    TestTargetA targetA1;

    TestTargetA targetA2;

    TestTargetB targetB;

    @Before
    public void setUp() {
        targetA1 = new TestTargetA();
        Powertape.inject(targetA1);

        targetA2 = new TestTargetA();
        Powertape.inject(targetA2);

        targetB = new TestTargetB();
        Powertape.inject(targetB);
    }

    @Test
    public void defaultScopeTest() {
        Assert.assertNotSame(targetA1.defaultScopeA, targetA1.defaultScopeB);
        Assert.assertNotSame(targetA1.defaultScopeA, targetA2.defaultScopeA);
        Assert.assertNotSame(targetA1.defaultScopeA, targetA2.defaultScopeB);
        Assert.assertNotSame(targetA1.defaultScopeB, targetA2.defaultScopeA);
        Assert.assertNotSame(targetA1.defaultScopeA, targetB.defaultScopeA);
        Assert.assertNotSame(targetA1.defaultScopeB, targetB.defaultScopeA);
        Assert.assertNotSame(targetA1.defaultScopeA, targetB.defaultScopeB);
        Assert.assertNotSame(targetA1.defaultScopeB, targetB.defaultScopeB);
    }

    @Test
    public void singletonScopeTest() {
        Assert.assertSame(targetA1.singletonScopeA, targetA1.singletonScopeB);
        Assert.assertSame(targetA1.singletonScopeA, targetA2.singletonScopeA);
        Assert.assertSame(targetA1.singletonScopeA, targetA2.singletonScopeB);
        Assert.assertSame(targetA1.singletonScopeB, targetA2.singletonScopeA);
        Assert.assertSame(targetA1.singletonScopeA, targetB.singletonScopeB);
        Assert.assertSame(targetA1.singletonScopeA, targetB.singletonScopeA);
        Assert.assertSame(targetA1.singletonScopeA, targetB.singletonScopeB);
        Assert.assertSame(targetA1.singletonScopeB, targetB.singletonScopeA);
    }

    @Test
    public void targetScopeTest() {
        Assert.assertSame(targetA1.targetScopeA, targetA1.targetScopeB);
        Assert.assertSame(targetA2.targetScopeA, targetA2.targetScopeB);
        Assert.assertNotSame(targetA1.targetScopeA, targetA2.targetScopeA);
        Assert.assertNotSame(targetA1.targetScopeB, targetA2.targetScopeB);
        Assert.assertNotSame(targetA1.targetScopeA, targetA2.targetScopeB);
        Assert.assertNotSame(targetA1.targetScopeB, targetA2.targetScopeA);
    }
}
