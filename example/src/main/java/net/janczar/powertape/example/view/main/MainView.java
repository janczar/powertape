package net.janczar.powertape.example.view.main;


import net.janczar.powertape.example.view.base.BaseView;

import java.util.List;

public interface MainView extends BaseView {

    void showMessages(List<String> messages);

}
