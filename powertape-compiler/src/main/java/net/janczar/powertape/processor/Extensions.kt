package net.janczar.powertape.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType


fun DeclaredType.asClassName(): ClassName {
    val typeElement = this.asElement()
    return if (typeElement is TypeElement) {
        typeElement.asClassName()
    } else {
        return Any::class.asClassName()
    }
}

fun DeclaredType.getProviderName() = (this.asElement() as TypeElement).getProviderName()

fun DeclaredType.getName() = (this.asElement() as TypeElement).getName()

fun TypeElement.getProviderName() = this.qualifiedName.toString() + "Provider"

fun TypeElement.getName() = this.qualifiedName.toString()

fun ClassName.getProvicerSimpleName() = this.simpleName() + "Provider"

fun ClassName.getInjectorSimpleName() = this.simpleName() + "Injector"