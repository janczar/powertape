package net.janczar.powertape.example.usecase;


import net.janczar.powertape.annotation.Provide;
import net.janczar.powertape.example.domain.UseCaseComposer;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AndroidUseCaseComposer implements UseCaseComposer {

    @Provide
    public AndroidUseCaseComposer() {
    }

    @Override
    public <T> ObservableTransformer<T, T> apply() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
