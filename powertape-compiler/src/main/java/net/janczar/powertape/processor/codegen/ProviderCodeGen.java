package net.janczar.powertape.processor.codegen;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.janczar.powertape.processor.Log;
import net.janczar.powertape.processor.provide.ConstructorProvider;
import net.janczar.powertape.processor.provide.Provider;
import net.janczar.powertape.processor.provide.ProviderDependency;
import net.janczar.powertape.processor.provide.ProviderType;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class ProviderCodeGen {

    public static JavaFile generateCode(final Provider provider) {

        String providedClassName = ((TypeElement)provider.providedClass.asElement()).getQualifiedName().toString();
        String classPackage = providedClassName.substring(0, providedClassName.lastIndexOf("."));
        String className = providedClassName.substring(providedClassName.lastIndexOf(".")+1) + "Provider";

        MethodSpec.Builder provideMethodBuilder = MethodSpec.methodBuilder("provide")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .returns(TypeName.get(provider.providedClass));

        StringBuilder params = new StringBuilder();
        for (ProviderDependency dependency : provider.dependencies) {
            String dependencyClassName = ((TypeElement)dependency.dependencyClass.asElement()).getQualifiedName().toString();
            params.append(dependency.name).append(", ");
            provideMethodBuilder.addStatement (
                dependencyClassName + " " + dependency.name + " = " + dependencyClassName + "Provider.provide()"
            );
        }
        if (params.length() > 0) {
            params.delete(params.length() - 2, params.length());
        }

        provideMethodBuilder.addStatement(providedClassName+" mock = net.janczar.powertape.Powertape.getMock(" + providedClassName + ".class)");
        provideMethodBuilder.beginControlFlow("if (mock != null)");
        provideMethodBuilder.addStatement("return mock");
        provideMethodBuilder.endControlFlow();

        if (provider.type == ProviderType.CONSTRUCTOR) {
            String instanceClassName = ((ConstructorProvider)provider).instanceClassName;
            provideMethodBuilder.addStatement (
                    instanceClassName+" instance = new "+instanceClassName+"("+params+")"
            );
            if (provider.hasInjectedFields) {
                provideMethodBuilder.addStatement (
                        instanceClassName+"Injector.inject(instance)"
                );
            }
            provideMethodBuilder.addStatement (
                    "return instance"
            );
        } else {
            provideMethodBuilder.addStatement (
                    "return null"
            );
        }

        TypeSpec providerClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(provideMethodBuilder.build())
                .build();


        return JavaFile.builder(classPackage, providerClass).build();
    }

}
