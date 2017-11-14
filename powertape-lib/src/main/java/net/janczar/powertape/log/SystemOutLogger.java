package net.janczar.powertape.log;


import net.janczar.powertape.log.Logger;

public class SystemOutLogger implements Logger {
    @Override
    public void i(String tag, String message) {
        System.out.println(tag+": "+message);
    }
}
