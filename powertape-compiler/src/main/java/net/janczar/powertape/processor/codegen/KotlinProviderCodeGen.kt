package net.janczar.powertape.processor.codegen

import com.squareup.kotlinpoet.*
import net.janczar.powertape.processor.asClassName
import net.janczar.powertape.processor.getProvicerSimpleName
import net.janczar.powertape.processor.model.Provider
import javax.annotation.processing.ProcessingEnvironment


class KotlinProviderCodeGen(
    processingEnvironment: ProcessingEnvironment
): CodeGen<Provider>(processingEnvironment) {

    override fun generate(item: Provider): FileSpec {

        val providedClass = item.providedClass.asClassName()
        val providerClassName = providedClass.getProvicerSimpleName()

        val companionSpec = TypeSpec
                .companionObjectBuilder()
                .addFunction(generateProvideFun(item, providedClass))
                .addFunction(generateCreateFun(item, providedClass))

        val classBuilder = TypeSpec
                .classBuilder(providerClassName)
                .companionObject(companionSpec.build())

        return FileSpec
                .builder(providedClass.packageName(), providerClassName)
                .addType(classBuilder.build())
                .build()
    }

    private fun generateProvideFun(provider: Provider, providedClass: ClassName): FunSpec {
        val funBuilder = FunSpec
                .builder("provide")
                .returns(providedClass)

        funBuilder.addStatement("return createInstance()")

        return funBuilder.build()
    }

    private fun generateCreateFun(provider: Provider, providedClass: ClassName): FunSpec {
        val funBuilder = FunSpec
                .builder("createInstance")
                .addModifiers(KModifier.PRIVATE)
                .returns(providedClass)

        val params = StringBuilder()
        for (dependency in provider.dependencies) {
            params.append(dependency.name).append(", ")
            val dependencyClass = dependency.type.asClassName()
            val dependencyProviderClass = ClassName(dependencyClass.packageName(), dependencyClass.getProvicerSimpleName())
            funBuilder.addStatement("val "+dependency.name+" = %T.provide()", dependencyProviderClass)
        }
        if (params.length > 1) {
            params.delete(params.length - 2, params.length)
        }

        val instanceClass = provider.instanceClass.asClassName()
        funBuilder.addStatement("return %T($params)", instanceClass)

        return funBuilder.build()
    }
}