package net.janczar.powertape.processor.codegen;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.janczar.powertape.processor.Log;
import net.janczar.powertape.processor.provide.ConstructorProvider;
import net.janczar.powertape.processor.provide.Provider;
import net.janczar.powertape.processor.provide.ProviderDependency;
import net.janczar.powertape.processor.provide.ProviderType;
import net.janczar.powertape.processor.provide.Scope;

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

        if (provider.scope == Scope.SINGLETON) {
            provideMethodBuilder.addStatement("return SingletonHolder.instance");
        } else {
            provideMethodBuilder.addStatement("return create()");
        }

        MethodSpec.Builder createMethodBuilder = MethodSpec.methodBuilder("create")
                                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                                .returns(TypeName.get(provider.providedClass));

        StringBuilder params = new StringBuilder();
        for (ProviderDependency dependency : provider.dependencies) {
            String dependencyClassName = ((TypeElement)dependency.dependencyClass.asElement()).getQualifiedName().toString();
            params.append(dependency.name).append(", ");
            createMethodBuilder.addStatement (
                dependencyClassName + " " + dependency.name + " = " + dependencyClassName + "Provider.provide()"
            );
        }
        if (params.length() > 0) {
            params.delete(params.length() - 2, params.length());
        }

        createMethodBuilder.addStatement(providedClassName+" mock = net.janczar.powertape.Powertape.getMock(" + providedClassName + ".class)");
        createMethodBuilder.beginControlFlow("if (mock != null)");
        createMethodBuilder.addStatement("return mock");
        createMethodBuilder.endControlFlow();

        if (provider.type == ProviderType.CONSTRUCTOR) {
            String instanceClassName = ((ConstructorProvider)provider).instanceClassName;
            createMethodBuilder.addStatement (
                    instanceClassName+" instance = new "+instanceClassName+"("+params+")"
            );
            if (provider.hasInjectedFields) {
                createMethodBuilder.addStatement (
                        instanceClassName+"Injector.inject(instance)"
                );
            }
            createMethodBuilder.addStatement (
                    "return instance"
            );
        } else {
            createMethodBuilder.addStatement (
                    "return null"
            );
        }

        TypeSpec.Builder providerClassBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        providerClassBuilder.addMethod(provideMethodBuilder.build());
        providerClassBuilder.addMethod(createMethodBuilder.build());

        if (provider.scope == Scope.SINGLETON) {

            TypeSpec.Builder singletonHolderClassBuilder = TypeSpec.classBuilder("SingletonHolder").addModifiers(Modifier.PRIVATE, Modifier.STATIC);
            singletonHolderClassBuilder.addField(
                FieldSpec.builder(TypeName.get(provider.providedClass), "instance", Modifier.PRIVATE, Modifier.STATIC)
                        .initializer("create()")
                        .build()
            );
            providerClassBuilder.addType(singletonHolderClassBuilder.build());
        }

        return JavaFile.builder(classPackage, providerClassBuilder.build()).build();
    }

}
