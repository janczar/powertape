package net.janczar.powertape.example.usecase

import android.util.Log
import io.reactivex.CompletableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.janczar.powertape.annotation.Provide
import net.janczar.powertape.example.domain.SchedulersProvider


class AndroidSchedulersProvider @Provide constructor() : SchedulersProvider {

    override fun <T> singleTransformer(): SingleTransformer<T, T> {
        return SingleTransformer { it ->
            it
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(this::onError)
        }
    }

    override fun completableTransformer(): CompletableTransformer {
        return CompletableTransformer { it ->
            it
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(this::onError)
        }
    }

    override fun <T> observableTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { it ->
            it
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(this::onError)
        }
    }

    private fun onError(error: Throwable) {
        Log.e("SchedulersProvider", "Error", error)
    }
}