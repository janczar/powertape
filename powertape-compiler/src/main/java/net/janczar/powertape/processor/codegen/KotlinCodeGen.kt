package net.janczar.powertape.processor.codegen

import net.janczar.powertape.processor.model.CodeGraph
import net.janczar.powertape.processor.model.ProviderType
import javax.annotation.processing.ProcessingEnvironment


class KotlinCodeGen(
        processingEnvironment: ProcessingEnvironment
) {

    private val providerCodeGen = KotlinProviderCodeGen(processingEnvironment)

    fun generate(codeGraph: CodeGraph) {
        codeGraph.providers.forEach{ it.value.filter { it.type != ProviderType.EXISTING }.forEach {
            providerCodeGen.generateCode(it)
        }}
    }
}