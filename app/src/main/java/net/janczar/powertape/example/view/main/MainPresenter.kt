package net.janczar.powertape.example.view.main

import net.janczar.powertape.annotation.Provide
import net.janczar.powertape.example.domain.usecase.GetMessagesUseCase
import net.janczar.powertape.example.view.base.BasePresenter


class MainPresenter @Provide constructor(
        private val getMessagesUseCase: GetMessagesUseCase
) : BasePresenter<MainView>() {

    fun loadMessages() {
        manage(
                getMessagesUseCase
                        .execute()
                        .subscribe { messages -> view?.showMessages(messages) }
        )
    }

}