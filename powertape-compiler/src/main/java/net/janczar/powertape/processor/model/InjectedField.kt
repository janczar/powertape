package net.janczar.powertape.processor.model

import javax.lang.model.type.DeclaredType


data class InjectedField(
        val containingType: DeclaredType,
        val name: String,
        val injectedType: DeclaredType,
        val type: InjectedFieldType
)