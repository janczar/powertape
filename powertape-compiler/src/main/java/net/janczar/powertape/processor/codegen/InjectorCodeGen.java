package net.janczar.powertape.processor.codegen;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.janczar.powertape.internal.InjectionContext;
import net.janczar.powertape.log.Log;
import net.janczar.powertape.processor.inject.InjectedField;
import net.janczar.powertape.processor.inject.Injector;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

public class InjectorCodeGen {

    public static JavaFile generateCode(final Injector injector) {

        String injectedClassName = ((TypeElement)injector.injectedClass.asElement()).getQualifiedName().toString();

        ClassName injectionContextClass = ClassName.get(InjectionContext.class);

        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(TypeName.get(injector.injectedClass), "target")
                .returns(TypeName.VOID);
        injectMethodBuilder.addStatement("inject($T.startWith(target), target)", injectionContextClass);

        MethodSpec.Builder injectInContextMethodBuilder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(injectionContextClass, "context", Modifier.FINAL)
                .addParameter(TypeName.get(injector.injectedClass), "target", Modifier.FINAL)
                .returns(TypeName.VOID);

        ClassName logClass = ClassName.get(Log.class);
        injectInContextMethodBuilder.addStatement("$T.i($S,$S)", logClass, "Powertape", "Injecting into type "+injectedClassName);

        for (InjectedField injectedField : injector.injectedFields) {
            ClassName injectedClass = ClassName.get((TypeElement)((DeclaredType)injectedField.type).asElement());
            ClassName providerClass = ClassName.get(injectedClass.packageName(), injectedClass.simpleName()+"Provider");
            injectInContextMethodBuilder.addStatement(
                "target.$L = $T.provide(context)", injectedField.name, providerClass
            );
            injectInContextMethodBuilder.addStatement("$T.i($S,$S + target.$L)", logClass, "Powertape", "target."+injectedField.name+" = ", injectedField.name);
        }

        String classPackage = injectedClassName.substring(0, injectedClassName.lastIndexOf("."));
        String className = injectedClassName.substring(injectedClassName.lastIndexOf(".")+1) + "Injector";

        TypeSpec providerClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(injectMethodBuilder.build())
                .addMethod(injectInContextMethodBuilder.build())
                .build();

        return JavaFile.builder(classPackage, providerClass).build();
    }

}
