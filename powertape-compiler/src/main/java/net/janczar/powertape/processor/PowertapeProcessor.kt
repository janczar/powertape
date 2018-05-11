package net.janczar.powertape.processor

import net.janczar.powertape.log.Log
import net.janczar.powertape.processor.codegen.KotlinCodeGen
import net.janczar.powertape.processor.finder.CodeFinder
import net.janczar.powertape.processor.model.InjectedFieldType
import net.janczar.powertape.processor.resolve.Resolver
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("net.janczar.powertape.annotation.Provide", "net.janczar.powertape.annotation.Inject")
class PowertapeProcessor: AbstractProcessor() {

    private lateinit var codeFinder: CodeFinder

    private lateinit var codeGen: KotlinCodeGen

    private lateinit var resolver: Resolver

    override fun process(annotations: MutableSet<out TypeElement>?, environment: RoundEnvironment?): Boolean {

        environment?.let {
            val codeGraph = codeFinder.searchCode(it)
            val typesMap = codeGraph.toTypesMap()

            Log.note("############## RESULTS ##############")
            //codeGraph.getInjectedFields().forEach { Log.note(" Field "+it.containingType.asElement().simpleName+"."+it.name+": "+it.injectedType.asElement().simpleName) }
            //codeGraph.getProvidedConstructors().forEach { Log.note(" Constructor "+it.providedClass.asElement().simpleName) }

            typesMap.getAll().forEach {
                val type = it
                Log.note("    " + type.type.getName())
                type.providers.forEach { Log.note("        has "+it.type+" provider") }
                type.injectedFields.filter { it.type == InjectedFieldType.FIELD }.forEach { Log.note("        has injected field "+it.name+": "+it.injectedType.asElement().simpleName) }
                type.injectedFields.filter { it.type == InjectedFieldType.PROPERTY }.forEach { Log.note("        has injected property "+it.name+": "+it.injectedType.asElement().simpleName) }
            }

            if (resolver.resolve(codeGraph)) {
                codeGen.generate(codeGraph)
            }
        }

        return true
    }

    override fun init(processingEnvironment: ProcessingEnvironment?) {
        processingEnvironment?.apply {
            Log.setMessager(messager)
            codeFinder = CodeFinder(elementUtils)
            codeGen = KotlinCodeGen(processingEnvironment)
            resolver = Resolver(elementUtils)
            TypeUtil.init(elementUtils)
        }
    }
}