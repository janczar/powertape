package net.janczar.powertape.processor.resolve

import net.janczar.powertape.processor.model.*


class InjectionStack {

    private val stack = ArrayList<InjectionPathItem>()

    fun push(injector: Injector, field: InjectedField) {
        stack.add(InjectorPathItem(injector, field))
    }

    fun push(provider: Provider, dependency: ProviderDependency) {
        stack.add(ProviderPathItem(provider, dependency))
    }

    fun pop() {
        stack.removeAt(stack.size - 1)
    }

    fun toPath(): InjectionPath = InjectionPath(stack)
}