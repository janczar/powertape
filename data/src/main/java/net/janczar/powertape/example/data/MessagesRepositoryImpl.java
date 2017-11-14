package net.janczar.powertape.example.data;


import net.janczar.powertape.annotation.Provide;
import net.janczar.powertape.example.domain.model.Message;
import net.janczar.powertape.example.domain.repository.MessagesRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class MessagesRepositoryImpl implements MessagesRepository {

    @Provide
    public MessagesRepositoryImpl() {
    }

    @Override
    public Observable<List<Message>> getMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("Hello, world!"));
        messages.add(new Message("Raz, dwa, trzy, cztery."));
        return Observable.defer(() -> Observable.just(messages));
    }
}
