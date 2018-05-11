package net.janczar.powertape.processor.model

import javax.lang.model.type.DeclaredType


data class ProviderScope(
        val type: ProviderScopeType,
        val scopeType: DeclaredType?
)

enum class ProviderScopeType {
    DEFAULT,
    SINGLETON,
    TYPE
}