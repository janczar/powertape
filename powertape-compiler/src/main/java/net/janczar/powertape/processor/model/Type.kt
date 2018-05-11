package net.janczar.powertape.processor.model

import javax.lang.model.type.DeclaredType


class Type(
    val type: DeclaredType
) {
    val injectedFields = ArrayList<InjectedField>()

    val providers = ArrayList<Provider>()
}