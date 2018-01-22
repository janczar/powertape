package net.janczar.powertape.example.domain

import io.reactivex.Observable


abstract class ObservableUseCase<in P, R>(private val schedulersProvider: SchedulersProvider) {

    private lateinit var observable: Observable<R>

    fun execute(param: P): Observable<R> {
        observable = createUseCaseObservable(param).compose(schedulersProvider.observableTransformer())
        return observable
    }

    protected abstract fun createUseCaseObservable(param: P): Observable<R>

}

abstract class VoidObservableUseCase<R>(schedulersProvider: SchedulersProvider) : ObservableUseCase<Unit, R>(schedulersProvider) {

    fun execute(): Observable<R> {
        return execute(Unit)
    }

    protected abstract fun createUseCaseObservable(): Observable<R>

    override fun createUseCaseObservable(param: Unit): Observable<R> {
        return createUseCaseObservable()
    }

}