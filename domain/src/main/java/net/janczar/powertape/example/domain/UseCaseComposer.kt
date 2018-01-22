package net.janczar.powertape.example.domain

import io.reactivex.ObservableTransformer


interface UseCaseComposer {
    fun <T> apply(): ObservableTransformer<T, T>
}