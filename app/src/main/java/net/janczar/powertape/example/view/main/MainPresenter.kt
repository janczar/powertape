package net.janczar.powertape.example.view.main

import net.janczar.powertape.annotation.Inject
import net.janczar.powertape.annotation.InjectProperty
import net.janczar.powertape.annotation.Provide
import net.janczar.powertape.example.domain.repository.UserRepository
import net.janczar.powertape.example.domain.usecase.GetMessagesUseCase
import net.janczar.powertape.example.domain.usecase.GetUserUseCase
import net.janczar.powertape.example.view.base.BasePresenter
import net.janczar.powertape.log.Log


class MainPresenter @Provide constructor(
        private val getMessagesUseCase: GetMessagesUseCase
        //,private val getUserUseCase: GetUserUseCase
) : BasePresenter<MainView>() {

    @InjectProperty
    private val test: JustForTest by inject()

    fun loadMessages() {
        Log.i("DEBUG", "PRESENTER TEST: "+test.text)
        manage(
                getMessagesUseCase
                        .execute()
                        .subscribe { messages -> view?.showMessages(messages) }
        )
    }

}