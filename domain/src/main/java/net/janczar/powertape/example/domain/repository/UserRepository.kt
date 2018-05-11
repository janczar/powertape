package net.janczar.powertape.example.domain.repository

import io.reactivex.Single
import net.janczar.powertape.example.domain.model.User


interface UserRepository {
    fun getUser(): Single<User>
}