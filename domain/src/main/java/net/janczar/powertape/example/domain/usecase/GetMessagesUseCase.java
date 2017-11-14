package net.janczar.powertape.example.domain.usecase;


import net.janczar.powertape.annotation.Inject;
import net.janczar.powertape.annotation.Provide;
import net.janczar.powertape.example.domain.UseCase;
import net.janczar.powertape.example.domain.UseCaseComposer;
import net.janczar.powertape.example.domain.model.Message;
import net.janczar.powertape.example.domain.repository.MessagesRepository;

import java.util.List;

import io.reactivex.Observable;

public class GetMessagesUseCase extends UseCase<Void, List<Message>> {

    @Inject
    MessagesRepository messagesRepository;

    @Provide
    public GetMessagesUseCase(final UseCaseComposer composer) {
        super(composer);
    }

    @Override
    protected Observable<List<Message>> createUseCaseObservable(Void param) {
        return messagesRepository.getMessages();
    }
}
