package net.janczar.powertape.view.base;


public class BasePresenter<T extends BaseView> {

    protected T view;

    protected void binf(T view) {
        this.view = view;
    }

    protected void unbind() {
        this.view = null;
    }

}
