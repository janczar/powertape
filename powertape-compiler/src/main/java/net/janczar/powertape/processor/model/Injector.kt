package net.janczar.powertape.processor.model

import javax.lang.model.type.DeclaredType


class Injector(
        val injectedClass: DeclaredType
) {
    val fields = ArrayList<InjectedField>()
}