package fi.helsinki.cs.tmc.intellij.holders;

import fi.helsinki.cs.tmc.core.TmcCore;

public class TmcCoreHolder {

    private static TmcCore core;

    private TmcCoreHolder() {}

    public static synchronized TmcCore get() {
        if (core == null) {
            return null;
        }
        return core;
    }

    public static synchronized void set(TmcCore newcore) {
        core = newcore;
    }
}
