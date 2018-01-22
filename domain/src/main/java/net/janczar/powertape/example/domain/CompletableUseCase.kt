package net.janczar.powertape.example.domain

import io.reactivex.Completable


abstract class CompletableUseCase<in P>(private val schedulersProvider: SchedulersProvider) {

    private lateinit var completable: Completable

    fun execute(param: P): Completable {
        completable = createUseCaseCompletable(param).compose(schedulersProvider.completableTransformer())
        return completable
    }

    protected abstract fun createUseCaseCompletable(param: P): Completable
}

abstract class VoidCompletableUseCase(schedulersProvider: SchedulersProvider): CompletableUseCase<Unit>(schedulersProvider) {

    fun execute(): Completable {
        return execute(Unit)
    }

    protected abstract fun createUseCaseCompletable(): Completable

    override fun createUseCaseCompletable(param: Unit): Completable {
        return createUseCaseCompletable()
    }

}