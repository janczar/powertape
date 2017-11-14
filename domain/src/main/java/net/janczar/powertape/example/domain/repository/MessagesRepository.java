package net.janczar.powertape.example.domain.repository;


import net.janczar.powertape.example.domain.model.Message;

import java.util.List;

import io.reactivex.Observable;

public interface MessagesRepository {
    Observable<List<Message>> getMessages();
}
