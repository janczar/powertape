package net.janczar.powertape.example.domain.usecase

import io.reactivex.Single
import net.janczar.powertape.annotation.Provide
import net.janczar.powertape.example.domain.SchedulersProvider
import net.janczar.powertape.example.domain.VoidSingleUseCase
import net.janczar.powertape.example.domain.model.User
import net.janczar.powertape.example.domain.repository.UserRepository


class GetUserUseCase @Provide constructor(
        schedulersProvider: SchedulersProvider,
        private val userRepository: UserRepository
): VoidSingleUseCase<User>(schedulersProvider) {
    override fun createUseCaseSingle(): Single<User> {
        return userRepository.getUser()
    }
}