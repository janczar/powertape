package net.janczar.powertape

import net.janczar.powertape.annotation.Inject
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredFunctions

class InjectDelegate<in R, out T>(
        private val targetClassName: String,
        private val injectedClassName: String
) {

    private var value: T? = null

    operator fun getValue(thisRef: R, property: KProperty<*>): T {
        if (property.annotations.find { it.annotationClass == Inject::class } == null) {
            throw RuntimeException("inject() delegate can be used only with properties annotated with @Inject")
        }
        value?.let {
            return it
        }
        return initialize()
    }

    private fun initialize(): T {
        val instance = createInstance()
        value = instance
        return instance
    }

    @Suppress("UNCHECKED_CAST")
    private fun createInstance(): T {
        val providerClassName = injectedClassName + "Provider"
        try {
            val providerClass: KClass<*> = Class.forName(providerClassName).kotlin
            val provideFunction = providerClass.companionObject?.getFunction("provide")
            val inst = provideFunction?.call(providerClass.companionObjectInstance) as T
            return inst
        } catch (e: Exception) {
            throw RuntimeException("Could not inject instance of "+injectedClassName, e)
        }
    }
}

fun KClass<*>.getFunction(name: String): KFunction<*>? {
    this.declaredFunctions.forEach {
        if (it.name == name) {
            return it
        }
    }
    throw NoSuchMethodException()
}