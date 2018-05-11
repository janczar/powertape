package net.janczar.powertape.example.view.main

import net.janczar.powertape.InjectDelegate

inline fun <reified R, reified T> MainPresenter.inject(): InjectDelegate<R, T> {
    val targetClassName = R::class.java.name
    val injectedClassName = T::class.java.name
    return InjectDelegate(targetClassName, injectedClassName)
}