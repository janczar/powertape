package net.janczar.powertape.processor.codegen;


import com.squareup.javapoet.ClassName;

import net.janczar.powertape.log.Log;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

public class CodeUtil {

    public static ClassName toClassName(final DeclaredType declaredType) {
        Element typeElement = declaredType.asElement();
        if (typeElement instanceof TypeElement) {
            return ClassName.get((TypeElement)typeElement);
        } else {
            Log.error("Not a class injectedType!", typeElement);
            return ClassName.get(Object.class);
        }
    }

}
