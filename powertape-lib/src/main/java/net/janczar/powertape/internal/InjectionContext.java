package net.janczar.powertape.internal;


import java.lang.ref.WeakReference;
import java.util.Arrays;

public final class InjectionContext {

    private final WeakReference[] context;

    private InjectionContext() {
        context = new WeakReference[0];
    }

    private InjectionContext(final Object object) {
        context = new WeakReference[] { new WeakReference(object) };
    }

    private InjectionContext(final WeakReference[] context) {
        this.context = context;
    }

    public static final InjectionContext empty() {
        return new InjectionContext();
    }

    public static final InjectionContext startWith(final Object object) {
        return new InjectionContext(object);
    }

    public final InjectionContext add(Object object) {
        WeakReference[] newContext = Arrays.copyOf(context, context.length+1);
        newContext[newContext.length-1] = new WeakReference(object);
        return new InjectionContext(newContext);
    }

    public <T> T getInstance(final Class<T> type) {
        for (int i=0; i<context.length; i++) {
            Object item = context[i].get();
            if (item != null && type.isAssignableFrom(item.getClass())) {
                return (T)context[i].get();
            }
        }
        return null;
    }
}
