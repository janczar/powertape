package net.janczar.powertape.processor.model

import net.janczar.powertape.annotation.Scope
import net.janczar.powertape.annotation.Singleton
import net.janczar.powertape.log.Log
import net.janczar.powertape.processor.TypeUtil
import net.janczar.powertape.processor.getName
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind

typealias ClassName = String

class CodeGraph {

    val providers = HashMap<ClassName, MutableList<Provider>>()

    val injectors = HashMap<ClassName, Injector>()

    fun addProvidedConstructor(constructor: ExecutableElement): Boolean {

        val classElement = constructor.enclosingElement as TypeElement

        val dependencies = ArrayList<ProviderDependency>()

        for (constructorArgument in constructor.parameters) {
            val parameterName = constructorArgument.simpleName.toString()
            if (constructorArgument.asType().kind != TypeKind.DECLARED) {
                Log.error(String.format("@Provide constructor parameter %s must not be of simple injectedType!", parameterName), constructor)
                continue
            }
            dependencies.add(ProviderDependency(parameterName, constructorArgument.asType() as DeclaredType))
        }

        val singletonAnnotation = constructor.getAnnotation(Singleton::class.java)
        val scopeAnnotation = constructor.getAnnotation(Scope::class.java)

        if (singletonAnnotation != null && scopeAnnotation != null) {
            Log.error("Singleton and Scope annotations can't be used at the same time!", constructor)
            return false
        }

        val scope =
                if (singletonAnnotation != null)
                    ProviderScope(ProviderScopeType.SINGLETON, null)
                else if (scopeAnnotation != null)
                    ProviderScope(ProviderScopeType.TYPE, TypeUtil.getScopeClass(constructor))
                else
                    ProviderScope(ProviderScopeType.DEFAULT, null)

        classElement.interfaces.forEach {
            add(
                Provider(
                    ProviderType.CONSTRUCTOR,
                    constructor,
                    it as DeclaredType,
                    classElement.asType() as DeclaredType,
                    scope,
                    dependencies)
            )
        }

        return add(
            Provider(
                ProviderType.CONSTRUCTOR,
                constructor,
                classElement.asType() as DeclaredType,
                classElement.asType() as DeclaredType,
                scope,
                dependencies)
        )
    }

    fun addInjectedField(field: VariableElement): Boolean {
        if (field.asType().kind != TypeKind.DECLARED) {
            Log.error(String.format("Field %s cannot be injected because it is of simple injectedType!", field.simpleName), field.enclosingElement)
            return false
        }
        if (field.modifiers.contains(Modifier.PRIVATE)) {
            Log.error(String.format("Field %s cannot be injected because it is private!", field.simpleName), field.enclosingElement)
            return false
        }
        if (field.modifiers.contains(Modifier.FINAL)) {
            Log.error(String.format("Field %s cannot be injected because it is final!", field.simpleName), field.enclosingElement)
            return false
        }

        return add(
            InjectedField(
                field.enclosingElement.asType() as DeclaredType,
                field.simpleName.toString(),
                field.asType() as DeclaredType,
                InjectedFieldType.FIELD
            )
        )
    }

    fun addInjectedProperty(property: ExecutableElement): Boolean {
        val propertyName = getPropertyName(property)

        if (property.returnType.kind != TypeKind.DECLARED) {
            Log.error(String.format("Field %s cannot be injected because it is of simple injectedType!", propertyName), property.enclosingElement)
            return false
        }

        return add(
                InjectedField(
                        property.enclosingElement.asType() as DeclaredType,
                        propertyName,
                        property.returnType as DeclaredType,
                        InjectedFieldType.PROPERTY
                )
        )
    }

    fun addExistingProvider(providedType: TypeElement, provider: TypeElement): Boolean {
        return add(
                Provider(
                        ProviderType.EXISTING,
                        providedType,
                        providedType.asType() as DeclaredType,
                        provider.asType() as DeclaredType,
                        ProviderScope(ProviderScopeType.DEFAULT, null),
                        emptyList())
        )
    }

    fun toTypesMap(): TypesMap {
        val map = TypesMap()
        injectors.forEach { map.getOrCreate(it.value.injectedClass).injectedFields.addAll(it.value.fields) }
        providers.forEach { it.value.forEach { map.getOrCreate(it.providedClass).providers.add(it) } }
        return map
    }

    private fun add(provider: Provider): Boolean {
        val providersList = providers[provider.providedClass.getName()] ?: ArrayList()
        providers[provider.providedClass.getName()] = providersList

        for (existing in providersList) {
            if (existing.type == provider.type) {
                if (existing.type == ProviderType.CONSTRUCTOR) {
                    if (existing.instanceClass.getName() == provider.instanceClass.getName()) {
                        return false
                    }
                } else {
                    return false
                }
            }
        }
        providersList.add(provider)
        return true
    }

    private fun add(injectedField: InjectedField):Boolean {
        val injector = injectors[injectedField.containingType.getName()] ?: Injector(injectedField.containingType)
        injectors[injectedField.containingType.getName()] = injector
        for (existing in injector.fields) {
            if (existing.name == injectedField.name) {
                return false
            }
        }
        injector.fields.add(injectedField)
        return true
    }

    private fun getPropertyName(element: Element) = element.simpleName.substring(3,4).toLowerCase() + element.simpleName.substring(4)
}