package net.janczar.powertape.example.view.base

import android.support.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


open class BasePresenter<V : BaseView> {

    private var compositeDisposable = CompositeDisposable()

    protected var view: V? = null

    fun attachView(view: V) {
        this.view = view
        onAttach()
    }

    @CallSuper
    open fun detachView() {
        compositeDisposable.clear()
        onDetach()
    }

    protected fun manage(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    open protected fun onAttach() = Unit

    open protected fun onDetach() = Unit

}