package net.janczar.powertape.example.view.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import net.janczar.powertape.Powertape;
import net.janczar.powertape.annotation.Inject;

import net.janczar.powertape.example.R;
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
    protected void onResume() {
        super.onResume();
        presenter.bind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unbind();
    }

    @OnClick(R.id.main_button_scopes)
    void onScopesButtonClick() {
        Intent intent = new Intent(this, ScopesActivity.class);
        startActivity(intent);
    }

    @Override
    public void showMessages(List<String> messages) {
    }
}
