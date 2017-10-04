package net.janczar.powertape;

import net.janczar.powertape.annotation.Inject;
import net.janczar.powertape.example.domain.repository.MessagesRepository;
import net.janczar.powertape.example.view.main.MainPresenter;
import net.janczar.powertape.example.view.main.MainView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    private static final List<String> MESSAGES = Arrays.asList("A", "B", "C", "D", "E");

    @Inject
    MainPresenter presenter;

    @Mock
    MainView view;

    @Mock
    MessagesRepository messagesRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(messagesRepository.getMessages()).thenReturn(MESSAGES);

        Powertape.mock(messagesRepository);
        Powertape.inject(this);

        presenter.bind(view);
    }

    @Test
    public void shouldDisplayListOfMessages() {
        presenter.loadMessages();
        verify(view).showMessages(MESSAGES);
    }
}
