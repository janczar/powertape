package net.janczar.powertape.example.domain;


public class CachableValue<T> {

    private final T value;

    private final boolean fromCache;

    private final boolean continued;

    public CachableValue(final T value, final boolean fromCache, final boolean continued) {
        this.value = value;
        this.fromCache = fromCache;
        this.continued = continued;
    }

    public T get() {
        return value;
    }

    public boolean isCached() {
        return fromCache;
    }

    public boolean isContinued() {
        return continued;
    }

}
