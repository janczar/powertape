package net.janczar.powertape.example.view.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import net.janczar.powertape.annotation.Inject
import net.janczar.powertape.annotation.InjectProperty
import net.janczar.powertape.annotation.TestInject
import net.janczar.powertape.example.R
import net.janczar.powertape.example.domain.model.Message


class MainActivity: AppCompatActivity(), MainView {

    @Inject
    lateinit var presenter2: MainPresenter

    @InjectProperty
    private val presenter: MainPresenter by inject()

    @InjectProperty
    private val test: JustForTest by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Powertape.inject(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
        presenter.loadMessages()

        presenter2.attachView(this)
        presenter2.loadMessages()
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun showMessages(messages: List<Message>) {
        Log.i("DEBUG", "MESSAGES")
        for (message in messages) {
            Log.i("DEBUG", "    "+message)
        }
        Log.i("DEBUG", "TEST: "+test.text)
    }
}