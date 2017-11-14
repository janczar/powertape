package net.janczar.powertape.example.view.base;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BasePresenter<T extends BaseView> {

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected T view;

    public void bind(T view) {
        this.view = view;
    }

    public void unbind() {
        this.view = null;
        disposables.dispose();
    }

    protected void manage(Disposable disposable) {
        disposables.add(disposable);
    }
}
