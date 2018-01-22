package net.janczar.powertape.example.view.scopes;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import net.janczar.powertape.Powertape;
import net.janczar.powertape.annotation.Inject;
import net.janczar.powertape.example.R;
import net.janczar.powertape.example.scopes.DefaultScopeExample;
import net.janczar.powertape.example.scopes.SingletonScopeExample;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScopesActivity extends AppCompatActivity implements ScopesView {

    @Inject
    ScopesPresenter presenter;

    @Inject
    DefaultScopeExample defaultScopeExample;

    @Inject
    SingletonScopeExample singletonScopeExample;

    @BindView(R.id.scopes_default)
    TextView scopeDefault;

    @BindView(R.id.scopes_singleton)
    TextView scopeSingleton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scopes);
        Powertape.inject(this);
        ButterKnife.bind(this);

        scopeDefault.setText(String.valueOf(defaultScopeExample.getId()));
        scopeSingleton.setText(String.valueOf(singletonScopeExample.getId()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
