package net.janczar.powertape.example.scopes;


import net.janczar.powertape.annotation.Provide;
import net.janczar.powertape.annotation.Singleton;

public class SingletonScopeExample {

    private static int counter = 1;

    private final int id = counter++;

    @Provide
    @Singleton
    SingletonScopeExample() {
    }

    public int getId() {
        return id;
    }

}
