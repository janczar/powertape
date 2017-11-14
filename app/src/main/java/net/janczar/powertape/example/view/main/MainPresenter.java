package net.janczar.powertape.example.view.main;

import net.janczar.powertape.annotation.Inject;
import net.janczar.powertape.annotation.Provide;
import net.janczar.powertape.example.domain.repository.MessagesRepository;
import net.janczar.powertape.example.domain.usecase.GetMessagesUseCase;
import net.janczar.powertape.example.view.base.BasePresenter;

import java.util.List;

public class MainPresenter extends BasePresenter<MainView> {

    @Inject
    GetMessagesUseCase getMessagesUseCase;

    @Provide
    public MainPresenter() {
    }

    public void loadMessages() {
        manage(
            getMessagesUseCase.execute(null)
                .subscribe(
                        messages -> {
                            view.showMessages(messages);
                        }
                ));

    }
}
