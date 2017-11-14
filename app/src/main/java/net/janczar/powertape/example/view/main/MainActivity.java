package net.janczar.powertape.example.view.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import net.janczar.powertape.Powertape;
import net.janczar.powertape.annotation.Inject;

import net.janczar.powertape.example.R;
import net.janczar.powertape.example.domain.model.Message;
import net.janczar.powertape.example.view.scopes.ScopesActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    MainPresenter presenter;

    private MessagesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Powertape.inject(this);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.bind(this);
        presenter.loadMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbind();
    }

    @OnClick(R.id.main_button_scopes)
    void onScopesButtonClick() {
        Intent intent = new Intent(this, ScopesActivity.class);
        startActivity(intent);
    }

    @Override
    public void showMessages(List<Message> messages) {
        Log.i("DEBUG", "MESSAGES FROM REPO:");
        for (Message message : messages) {
            Log.i("DEBUG", "    "+message.message);
        }
    }
}
