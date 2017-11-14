package net.janczar.powertape.example.domain;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Action;

public abstract class UseCase<P, R> {

    protected final UseCaseComposer useCaseComposer;

    protected final Map<P, Observable<R>> observablesMap = new HashMap<>();

    protected boolean cacheObservable = true;

    protected UseCase(final UseCaseComposer useCaseComposer) {
        this.useCaseComposer = useCaseComposer;
    }

    protected void setCacheObservable(final boolean cacheObservable) {
        this.cacheObservable = cacheObservable;
    }

    protected abstract Observable<R> createUseCaseObservable(final P param);

    public boolean isRunning(P param) {
        return observablesMap.containsKey(param);
    }

    public int getRunningCount() {
        return observablesMap.size();
    }

    public Observable<R> execute(final P param) {

        Observable<R> observable = observablesMap.get(param);

        if (observable == null || !cacheObservable) {

            try {
                observable = createUseCaseObservable(param);
            } catch (Exception e) {
                observable = Observable.error(e);
            }

            if (useCaseComposer != null) {
                observable = observable.compose(useCaseComposer.<R>apply());
            }
            observable = observable.doOnDispose(new OnTerminateAction(param));
            observablesMap.put(param, observable);
        }

        return observable;
    }

    private class OnTerminateAction implements Action {

        private P param;

        OnTerminateAction(P param) {
            this.param = param;
        }

        @Override
        public void run() throws Exception {
            observablesMap.remove(param);
        }
    }

    protected <T> CachableValue<T> asCachable(T value) {
        return new CachableValue<>(value, false, false);
    }
}
