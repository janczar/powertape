package net.janczar.powertape.example.scopes;


import net.janczar.powertape.annotation.Provide;

public class DefaultScopeExample {

    private static int counter = 1;

    private final int id = counter++;

    @Provide
    DefaultScopeExample() {
    }

    public int getId() {
        return id;
    }

}
