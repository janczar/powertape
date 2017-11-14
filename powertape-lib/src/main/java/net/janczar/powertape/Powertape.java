package net.janczar.powertape;


import net.janczar.powertape.log.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Powertape {

    private static final Map<String, Method> injectMethods = new HashMap<>();

    private static final Map<String, Object> mocks = new HashMap<>();

    public static void inject(final Object target) {

        String className = target.getClass().getName();
        Method injectMethod = injectMethods.get(className);
        if (injectMethod == null) {
            String injectorClassName = className+"Injector";
            try {
                Class injectorClass = Class.forName(injectorClassName);
                injectMethod = injectorClass.getDeclaredMethod("inject", target.getClass());
                injectMethods.put(className, injectMethod);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not find Injector code for "+className+". Please make sure to add annotationProcessor to your build gradle and rebuild project.");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Invalid injector code for "+className+" - no inject method.");
            }
        }

        try {
            injectMethod.invoke(null, target);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Could not inkoke inject code for "+className+"!", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not inkoke inject code for "+className+"!", e);
        }

    }

    public static void mock(final Object mock) {
        if (mock == null) {
            throw new IllegalArgumentException("Mock is null!");
        }
        String mockClassName = mock.getClass().getName();
        mocks.put(mockClassName, mock);
        Log.i("Powertape", "Registering mock for class "+mockClassName);
        if (mockClassName.contains("$$")) {
            Class superClass = mock.getClass().getSuperclass();
            if (superClass != null) {
                mocks.put(superClass.getName(), mock);
                Log.i("Powertape", "Registering mock for class "+superClass.getName());
            }
        }
        for (Class<?> interfaceClass : mock.getClass().getInterfaces()) {
            mocks.put(interfaceClass.getName(), mock);
            Log.i("Powertape", "Registering mock for interface "+interfaceClass.getName());
        }
    }

    public static <T> T getMock(Class<T> mockClass) {
        Object registeredMock = mocks.get(mockClass.getName());
        if (registeredMock != null && mockClass.isInstance(registeredMock)) {
            Log.i("Powertape", "Using mock for class "+mockClass.getName());
            return (T)registeredMock;
        }
        return null;
    }
}
