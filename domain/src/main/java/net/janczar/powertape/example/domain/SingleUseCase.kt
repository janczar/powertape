package net.janczar.powertape.example.domain
import io.reactivex.Single


abstract class SingleUseCase<in P, R>(private val schedulersProvider: SchedulersProvider) {

    private lateinit var single: Single<R>

    fun execute(param: P): Single<R> {
        single = createUseCaseSingle(param).compose(schedulersProvider.singleTransformer())
        return single
    }

    protected abstract fun createUseCaseSingle(param: P): Single<R>

}

abstract class VoidSingleUseCase<R>(schedulersProvider: SchedulersProvider): SingleUseCase<Unit, R>(schedulersProvider) {

    fun execute(): Single<R> {
        return execute(Unit)
    }

    protected abstract fun createUseCaseSingle(): Single<R>

    override fun createUseCaseSingle(param: Unit) = createUseCaseSingle()

}