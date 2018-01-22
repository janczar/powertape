package net.janczar.powertape.example.domain.repository

import io.reactivex.Single
import net.janczar.powertape.example.domain.model.Message


interface MessagesRepository {
    fun getMessages(): Single<List<Message>>
}