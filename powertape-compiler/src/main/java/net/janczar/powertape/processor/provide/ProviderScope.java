package net.janczar.powertape.processor.provide;


import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class ProviderScope {

    public final Type scopeType;

    public final DeclaredType scopeClass;

    public ProviderScope(final Type scopeType) {
        this.scopeType = scopeType;
        this.scopeClass = null;
    }

    public ProviderScope(final Type scopeType, final DeclaredType scopeClass) {
        this.scopeType = scopeType;
        this.scopeClass = scopeClass;
    }

    public static enum Type {
        DEFAULT,
        SINGLETON,
        TYPE
    }
}
