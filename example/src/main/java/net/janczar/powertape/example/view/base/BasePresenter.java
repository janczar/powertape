package net.janczar.powertape.example.view.base;


public class BasePresenter<T extends BaseView> {

    protected T view;

    public void bind(T view) {
        this.view = view;
    }

    public void unbind() {
        this.view = null;
    }

}
