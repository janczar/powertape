package net.janczar.powertape;

import net.janczar.powertape.annotation.Inject;
import net.janczar.powertape.example.domain.UseCaseComposer;
import net.janczar.powertape.example.domain.model.Message;
import net.janczar.powertape.example.domain.repository.MessagesRepository;
import net.janczar.powertape.example.domain.usecase.GetMessagesUseCase;
import net.janczar.powertape.example.view.main.MainPresenter;
import net.janczar.powertape.example.view.main.MainView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    private static final List<Message> MESSAGES = Arrays.asList(
            new Message("A"),
            new Message("B"),
            new Message("C"),
            new Message("D"),
            new Message("E")
    );

    @Inject
    MainPresenter presenter;

    @Mock
    MainView view;

    @Mock
    MessagesRepository messagesRepository;

    @Mock
    UseCaseComposer useCaseComposer;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        //when(getMessagesUseCase.execute(any())).thenReturn(Observable.just(MESSAGES));
        when(messagesRepository.getMessages()).thenReturn(Observable.just(MESSAGES));
        when(useCaseComposer.apply()).thenReturn(observable -> observable);

        //Powertape.mock(getMessagesUseCase);
        Powertape.mock(messagesRepository);
        Powertape.mock(useCaseComposer);
        Powertape.inject(this);

        presenter.bind(view);
    }

    @Test
    public void shouldDisplayListOfMessages() {
        presenter.loadMessages();
        verify(view).showMessages(MESSAGES);
    }
}
