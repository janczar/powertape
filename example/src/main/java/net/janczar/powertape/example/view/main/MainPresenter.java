package net.janczar.powertape.example.view.main;

import net.janczar.powertape.annotation.Inject;
import net.janczar.powertape.annotation.Provide;
import net.janczar.powertape.example.domain.repository.MessagesRepository;
import net.janczar.powertape.example.view.base.BasePresenter;

import java.util.List;

public class MainPresenter extends BasePresenter<MainView> {

    @Inject
    MessagesRepository messagesRepository;

    @Provide
    public MainPresenter() {
    }

    public void loadMessages() {
        List<String> messages = messagesRepository.getMessages();
        view.showMessages(messages);
    }
}
