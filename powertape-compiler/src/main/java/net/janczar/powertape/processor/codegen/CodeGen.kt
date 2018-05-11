package net.janczar.powertape.processor.codegen

import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.ProcessingEnvironment


abstract class CodeGen<T> (
    processingEnvironment: ProcessingEnvironment
) {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private val outputDir = File(processingEnvironment.options[KAPT_KOTLIN_GENERATED_OPTION_NAME])

    fun generateCode(item: T) {
        val fileSpec = generate(item)
        fileSpec.writeTo(outputDir)
    }

    protected abstract fun generate(item: T): FileSpec
}