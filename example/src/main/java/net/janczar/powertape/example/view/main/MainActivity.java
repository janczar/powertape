package net.janczar.powertape.example.view.main;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import net.janczar.powertape.Powertape;
import net.janczar.powertape.annotation.Inject;

import net.janczar.powertape.example.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

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

        adapter = new MessagesAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.bind(this);
        presenter.loadMessages();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unbind();
    }

    @Override
    public void showMessages(List<String> messages) {
        adapter.swap(messages);
    }
}
