package net.janczar.powertape;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Powertape {

    private static Map<String, Method> injectMethods = new HashMap<>();

    public static void inject(Object target) {

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
            throw new RuntimeException("Could not inkoke inject code for "+className+"!");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not inkoke inject code for "+className+"!");
        }

    }

}
