package net.janczar.powertape.processor.model


interface InjectionPathItem

data class ProviderPathItem(
        val provider: Provider,
        val dependency: ProviderDependency
) : InjectionPathItem

data class InjectorPathItem(
        val injector: Injector,
        val field: InjectedField
) : InjectionPathItem