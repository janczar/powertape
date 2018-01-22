package net.janczar.powertape.example.domain.usecase

import io.reactivex.Single
import net.janczar.powertape.annotation.Provide
import net.janczar.powertape.example.domain.SchedulersProvider
import net.janczar.powertape.example.domain.VoidSingleUseCase
import net.janczar.powertape.example.domain.model.Message
import net.janczar.powertape.example.domain.repository.MessagesRepository


class GetMessagesUseCase @Provide constructor(
        schedulersProvider: SchedulersProvider,
        private val messagesRepository: MessagesRepository
): VoidSingleUseCase<List<Message>>(schedulersProvider) {
    override fun createUseCaseSingle(): Single<List<Message>> {
        return messagesRepository.getMessages()
    }
}