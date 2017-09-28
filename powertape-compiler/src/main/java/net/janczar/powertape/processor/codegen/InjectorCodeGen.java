package net.janczar.powertape.processor.codegen;


import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.janczar.powertape.processor.inject.InjectedField;
import net.janczar.powertape.processor.inject.Injector;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class InjectorCodeGen {

    public static JavaFile generateCode(final Injector injector) {

        String injectedClassName = ((TypeElement)injector.injectedClass.asElement()).getQualifiedName().toString();

        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(TypeName.get(injector.injectedClass), "target")
                .returns(TypeName.VOID);

        for (InjectedField injectedField : injector.injectedFields) {
            injectMethodBuilder.addStatement(
                "target." + injectedField.name + " = " + injectedField.className + "Provider.provide()"
            );
        }

        String classPackage = injectedClassName.substring(0, injectedClassName.lastIndexOf("."));
        String className = injectedClassName.substring(injectedClassName.lastIndexOf(".")+1) + "Injector";

        TypeSpec providerClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(injectMethodBuilder.build())
                .build();

        return JavaFile.builder(classPackage, providerClass).build();
    }

}
