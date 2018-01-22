package net.janczar.powertape.example.domain

import io.reactivex.CompletableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer


interface SchedulersProvider {

    fun completableTransformer(): CompletableTransformer

    fun <T> observableTransformer(): ObservableTransformer<T, T>

    fun <T> singleTransformer(): SingleTransformer<T, T>

}