package net.janczar.powertape.example.view.main

import net.janczar.powertape.example.domain.model.Message
import net.janczar.powertape.example.view.base.BaseView


interface MainView : BaseView {
    fun showMessages(messages: List<Message>)
}