package net.janczar.powertape.example.view.main;

import net.janczar.powertape.annotation.Provide;
import net.janczar.powertape.example.domain.repository.MessagesRepository;
import net.janczar.powertape.example.view.base.BasePresenter;

import java.util.List;

public class MainPresenter extends BasePresenter<MainView> {

    private final MessagesRepository messagesRepository;

    @Provide
    public MainPresenter(final MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    public void loadMessages() {
        List<String> messages = messagesRepository.getMessages();
        view.showMessages(messages);
    }
}
