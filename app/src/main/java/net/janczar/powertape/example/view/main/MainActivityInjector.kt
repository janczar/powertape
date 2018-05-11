package net.janczar.powertape.example.view.main

import net.janczar.powertape.InjectDelegate
import net.janczar.powertape.example.data.MessagesRepositoryImpl
import net.janczar.powertape.example.domain.usecase.GetMessagesUseCase
import net.janczar.powertape.example.usecase.AndroidSchedulersProvider

//class MainPresenterProvider {
//    companion object {
//        fun provide(): MainPresenter = MainPresenter(
//                GetMessagesUseCase(
//                        AndroidSchedulersProvider(),
//                        MessagesRepositoryImpl()
//                ))
//    }
//}

class JustForTestProvider {
    companion object {
        fun provide(): JustForTest = JustForTest("Hello there! General Kenobi!")
    }
}

inline fun <reified R, reified T> MainActivity.inject(): InjectDelegate<R, T> {
    val targetClassName = R::class.java.name
    val injectedClassName = T::class.java.name
    return InjectDelegate(targetClassName, injectedClassName)
}