package fi.helsinki.cs.tmc.intellij.holders;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.langs.util.TaskExecutor;
import fi.helsinki.cs.tmc.langs.util.TaskExecutorImpl;

public class TmcCoreHolder {

    private static TmcCore core;

    private TmcCoreHolder() {}

    public static synchronized TmcCore get() {
        if (core == null) {
            TaskExecutor tmcLangs = new TaskExecutorImpl();
            core = new TmcCore(TmcSettingsManager.get(), tmcLangs);
        }
        return core;
    }

    public static synchronized void setup() {
        if (core == null) {
            TaskExecutor tmcLangs = new TaskExecutorImpl();
            core = new TmcCore(TmcSettingsManager.get(), tmcLangs);
        }
    }

    public static synchronized void set(TmcCore newcore) {
        core = newcore;
    }


    //Testing only
    public static synchronized TmcCore testGet() {
        if (core == null) {
            TaskExecutor tmcLangs = new TaskExecutorImpl();
            core = new TmcCore(new SettingsTmc("https://tmc.mooc.fi/staging/org/tmc-intellij", "intellij-student", "intellij-tmc"), tmcLangs);
        }
        return core;
    }
}
