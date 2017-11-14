package net.janczar.powertape.processor.codegen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.janczar.powertape.Powertape;
import net.janczar.powertape.internal.InjectionContext;
import net.janczar.powertape.internal.ScopeMap;
import net.janczar.powertape.log.Log;
import net.janczar.powertape.processor.provide.ConstructorProvider;
import net.janczar.powertape.processor.provide.Provider;
import net.janczar.powertape.processor.provide.ProviderDependency;
import net.janczar.powertape.processor.provide.ProviderScope;
import net.janczar.powertape.processor.provide.ProviderType;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class ProviderCodeGen {

    public static JavaFile generateCode(final Provider provider) {

        Log.note("Generating provider for "+provider.providedClass.toString());

        ClassName injectionContextClass = ClassName.get(InjectionContext.class);
        ClassName providedClass = CodeUtil.toClassName(provider.providedClass);
        ClassName scopeType = null;
        if (provider.providerScope.scopeType == ProviderScope.Type.TYPE) {
            scopeType = CodeUtil.toClassName(provider.providerScope.scopeClass);
        }

        MethodSpec.Builder provideMethodBuilder = MethodSpec.methodBuilder("provide")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(injectionContextClass, "context", Modifier.FINAL)
                .returns(TypeName.get(provider.providedClass));

        if (provider.providerScope.scopeType == ProviderScope.Type.TYPE) {
            provideMethodBuilder.addStatement("$T scope = context.getInstance($T.class)", scopeType, scopeType);
            provideMethodBuilder.beginControlFlow("if (scope == null)");
            provideMethodBuilder.addStatement("throw new $T($S)", ClassName.get(IllegalStateException.class), "There is no instance of required class "+scopeType.reflectionName()+" in injection context!");
            provideMethodBuilder.endControlFlow();
            provideMethodBuilder.addStatement("$T instance = scopeMap.getInstance(scope)", providedClass);
            provideMethodBuilder.beginControlFlow("if (instance == null)");
            provideMethodBuilder.addStatement("instance = create(context)");
            provideMethodBuilder.addStatement("scopeMap.put(scope, instance)");
            provideMethodBuilder.endControlFlow();
            provideMethodBuilder.addStatement("return instance");
        } else if (provider.providerScope.scopeType == ProviderScope.Type.SINGLETON) {
            provideMethodBuilder.addStatement("return SingletonHolder.instance");
        } else {
            provideMethodBuilder.addStatement("return create(context)");
        }

        MethodSpec.Builder createMethodBuilder = MethodSpec.methodBuilder("create")
                                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                                .addParameter(injectionContextClass, "context", Modifier.FINAL)
                                .returns(TypeName.get(provider.providedClass));

        StringBuilder params = new StringBuilder();
        for (ProviderDependency dependency : provider.dependencies) {
            ClassName dependencyClass = CodeUtil.toClassName(dependency.dependencyClass);
            ClassName dependencyProviderClass = ClassName.get(dependencyClass.packageName(), dependencyClass.simpleName() + "Provider");
            params.append(dependency.name).append(", ");
            createMethodBuilder.addStatement (
                "$T " + dependency.name + " = $T.provide(context)", dependencyClass, dependencyProviderClass
            );
        }
        if (params.length() > 0) {
            params.delete(params.length() - 2, params.length());
        }

        createMethodBuilder.addStatement("$T mock = $T.getMock($T.class)", providedClass, ClassName.get(Powertape.class), providedClass);
        createMethodBuilder.beginControlFlow("if (mock != null)");
        createMethodBuilder.addStatement("return mock");
        createMethodBuilder.endControlFlow();

        if (provider.type == ProviderType.CONSTRUCTOR) {
            ClassName instanceClass = ClassName.get((TypeElement)((ConstructorProvider)provider).instanceClass.asElement());
            createMethodBuilder.addStatement ("$T instance = new $T("+params+")", providedClass, instanceClass);
            if (provider.hasInjectedFields) {
                ClassName injectorClass = ClassName.get(instanceClass.packageName(), instanceClass.simpleName()+"Injector");
                createMethodBuilder.addStatement ("$T.inject(context.add(instance), instance)", injectorClass);
            }
            createMethodBuilder.addStatement (
                    "return instance"
            );
        } else {
            createMethodBuilder.addStatement (
                    "return null"
            );
        }

        TypeSpec.Builder providerClassBuilder = TypeSpec.classBuilder(providedClass.simpleName()+"Provider")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        providerClassBuilder.addMethod(provideMethodBuilder.build());
        providerClassBuilder.addMethod(createMethodBuilder.build());

        if (provider.providerScope.scopeType == ProviderScope.Type.TYPE) {
            ClassName scopeMapType = ClassName.get(ScopeMap.class);
            providerClassBuilder.addField(
                    FieldSpec.builder(ParameterizedTypeName.get(scopeMapType, scopeType, providedClass), "scopeMap", Modifier.PRIVATE, Modifier.STATIC)
                            .initializer("new $T<>()", scopeMapType)
                            .build());
        }

        if (provider.providerScope.scopeType == ProviderScope.Type.SINGLETON) {

            TypeSpec.Builder singletonHolderClassBuilder = TypeSpec.classBuilder("SingletonHolder").addModifiers(Modifier.PRIVATE, Modifier.STATIC);
            singletonHolderClassBuilder.addField(
                FieldSpec.builder(TypeName.get(provider.providedClass), "instance", Modifier.PRIVATE, Modifier.STATIC)
                        .initializer("create($T.empty())", injectionContextClass)
                        .build()
            );
            providerClassBuilder.addType(singletonHolderClassBuilder.build());
        }

        return JavaFile.builder(providedClass.packageName(), providerClassBuilder.build()).build();
    }

}
