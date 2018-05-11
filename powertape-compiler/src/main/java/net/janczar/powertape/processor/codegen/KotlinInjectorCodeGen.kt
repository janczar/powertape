package net.janczar.powertape.processor.codegen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import net.janczar.powertape.processor.asClassName
import net.janczar.powertape.processor.getInjectorSimpleName
import net.janczar.powertape.processor.model.Injector
import javax.annotation.processing.ProcessingEnvironment


class KotlinInjectorCodeGen(
        processingEnvironment: ProcessingEnvironment
): CodeGen<Injector>(processingEnvironment) {

    override fun generate(item: Injector): FileSpec {

        val injectedClass = item.injectedClass.asClassName()
        val injectorClassName = injectedClass.getInjectorSimpleName()

        val companionSpec = TypeSpec
                .companionObjectBuilder()

        val classBuilder = TypeSpec
                .classBuilder(injectorClassName)
                .companionObject(companionSpec.build())

        return FileSpec
                .builder(injectedClass.packageName(), injectorClassName)
                .addType(classBuilder.build())
                .build()
    }

    private fun generateInjectFun() {

    }
}