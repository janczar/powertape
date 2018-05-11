package net.janczar.powertape.processor.model

import net.janczar.powertape.processor.getName
import javax.lang.model.type.DeclaredType


class InjectionPath(
        private val items: List<InjectionPathItem>
) {
    val size: Int
        get() = items.size

    fun hasScope(type: DeclaredType) =
            items.filter { it is InjectorPathItem }
                    .map { it as InjectorPathItem }
                    .any { it.injector.injectedClass.getName() == type.getName() }
}