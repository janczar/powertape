package net.janczar.powertape.example.data.messages;


import net.janczar.powertape.example.domain.repository.MessagesRepository;

import java.util.ArrayList;
import java.util.List;
import net.janczar.powertape.annotation.Provide;

public class MessagesRepositoryImpl implements MessagesRepository {

    @Provide
    public MessagesRepositoryImpl() {
    }

    @Override
    public List<String> getMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("Hello there");
        messages.add("These are");
        messages.add("some awesome");
        messages.add("example");
        messages.add("items");
        return messages;
    }
}
