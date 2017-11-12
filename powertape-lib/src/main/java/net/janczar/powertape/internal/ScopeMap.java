package net.janczar.powertape.internal;


import java.util.ArrayList;
import java.util.List;

public final class ScopeMap<C,P> {

    private final List<ScopeMapEntry> entries = new ArrayList<>();

    public final synchronized P getInstance(final C context) {
        for (ScopeMapEntry entry : entries) {
            if (entry.context == context) {
                return entry.instance;
            }
        }
        return null;
    }

    public final synchronized void put(final C context, final P instance) {
        entries.add(new ScopeMapEntry(context, instance));
    }

    private class ScopeMapEntry {
        final C context;
        final P instance;
        ScopeMapEntry(final C context, final P instance) {
            this.context = context;
            this.instance = instance;
        }
    }
}
