package net.janczar.powertape.processor.model

import javax.lang.model.type.DeclaredType


data class ProviderDependency(
    val name: String,
    val type: DeclaredType
)