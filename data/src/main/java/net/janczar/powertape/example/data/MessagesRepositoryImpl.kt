package net.janczar.powertape.example.data

import io.reactivex.Single
import net.janczar.powertape.annotation.Provide
import net.janczar.powertape.example.domain.model.Message
import net.janczar.powertape.example.domain.repository.MessagesRepository
import java.util.*


class MessagesRepositoryImpl @Provide constructor(): MessagesRepository {
    override fun getMessages(): Single<List<Message>> {
        val messages = ArrayList<Message>()
        messages.add(Message("Hello world"))
        messages.add(Message("Raz dwa trzy"))
        return Single.fromCallable { messages }
    }
}