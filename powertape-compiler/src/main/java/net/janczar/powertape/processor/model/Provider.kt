package net.janczar.powertape.processor.model

import javax.lang.model.element.Element
import javax.lang.model.type.DeclaredType


class Provider(
        val type: ProviderType,
        val element: Element,
        val providedClass: DeclaredType,
        val instanceClass: DeclaredType,
        val scope: ProviderScope,
        val dependencies: List<ProviderDependency>
)