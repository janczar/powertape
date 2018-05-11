package net.janczar.powertape.processor.resolve

import net.janczar.powertape.log.Log
import net.janczar.powertape.processor.getName
import net.janczar.powertape.processor.model.*
import javax.lang.model.element.Element
import javax.lang.model.type.DeclaredType
import javax.lang.model.util.Elements


class Resolver(
        private val elements: Elements
) {

    private var result = true

    private lateinit var codeGraph: CodeGraph

    @Synchronized
    fun resolve(codeGraph: CodeGraph): Boolean {
        this.codeGraph = codeGraph
        result = true

        for (provideEntry in codeGraph.providers) {
            val providersList = provideEntry.value.filter { it.type != ProviderType.EXISTING }
            if (providersList.size > 1) {
                error("Only one provider is allowed per class/interface. Multi-provider support is planned in the feature.", provideEntry.key)
            }
            providersList.forEach { resolveProvider(it) }
        }

        codeGraph.injectors.values.forEach { resolveInjector(it) }

        return result
    }

    private fun resolveProvider(provider: Provider) {
        if (provider.scope.type == ProviderScopeType.TYPE) {
            verifyScope(provider)
        }
        for (dependency in provider.dependencies) {
            if (!isTypeProvided(dependency.type)) {
                when (provider.type) {
                    ProviderType.CONSTRUCTOR -> error("Constructor's argument of type ${dependency.type.getName()} is not provided by any provider!", provider.providedClass)
                    else -> error("Provider's dependency of type ${dependency.type.getName()} is not provided by any provider!", provider.providedClass)
                }
            }
        }
    }

    private fun resolveInjector(injector: Injector) {
        for (field in injector.fields) {
            if (!isTypeProvided(field.injectedType)) {
                error("Field ${field.name} of type ${field.injectedType.getName()} is not provided by any provider!", field.injectedType)
            }
        }
    }

    private fun isTypeProvided(declaredType: DeclaredType): Boolean {
        return codeGraph.providers[declaredType.getName()]?.let { it.size > 0 } ?: false
    }

    private fun findInjectionPaths(provider: Provider): List<InjectionPath> {
        val paths = ArrayList<InjectionPath>()
        val stack = InjectionStack()
        findInjectionPaths(provider, stack, paths)
        return paths
    }

    private fun findInjectionPaths(provider: Provider, stack: InjectionStack, paths: MutableList<InjectionPath>) {
        var anyParents = false

        for (providerList in codeGraph.providers.values) {
            for (parentProvider in providerList.filter { it != provider }) {
                for (dependency in parentProvider.dependencies.filter { it.type.getName() == provider.providedClass.getName() }) {
                    stack.push(parentProvider, dependency)
                    findInjectionPaths(parentProvider, stack, paths)
                    stack.pop()
                    anyParents = true
                }
            }
        }

        for (injector in codeGraph.injectors.values) {
            for (field in injector.fields.filter { it.injectedType.getName() == provider.providedClass.getName() }) {
                stack.push(injector, field)
                findInjectionPaths(injector, stack, paths)
                stack.pop()
                anyParents = true
            }
        }

        if (!anyParents) {
            paths.add(stack.toPath())
        }
    }

    private fun findInjectionPaths(injector: Injector, stack: InjectionStack, paths: MutableList<InjectionPath>) {
        var anyParents = false
        for (providerList in codeGraph.providers.values) {
            for (provider in providerList.filter { it.providedClass.getName() == injector.injectedClass.getName() }) {
                    findInjectionPaths(provider, stack, paths)
                    anyParents = true
            }
        }
        if (!anyParents) {
            paths.add(stack.toPath())
        }
    }

    private fun verifyScope(provider: Provider) {
        val wantedScope = provider.scope.scopeType ?: return
        val paths = findInjectionPaths(provider)
        paths.filter { it.size > 0 && it.hasScope(wantedScope) }.takeIf { it.isNotEmpty() }?.also {
            val errorMessage = StringBuilder()
            errorMessage.append("Class requires scope of injectedType ${wantedScope.getName()}, however there are ${it.size} injection paths that do not provide instance of that class:\n\n")
            for (path in it) {
                errorMessage.append("- ").append(path.toString()).append("\n")
            }
            Log.error(errorMessage.toString(), provider.element)
        }

    }

    private fun error(message: String) {
        Log.error(message)
        result = false
    }

    private fun error(message: String, typeName: ClassName) {
        error(message, elements.getTypeElement(typeName))
    }

    private fun error(message: String, type: DeclaredType) {
        error(message, elements.getTypeElement(type.getName()))
    }

    private fun error(message: String, element: Element) {
        Log.error(message, element)
        result = false
    }
}