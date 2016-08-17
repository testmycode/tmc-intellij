package fi.helsinki.cs.tmc.intellij.holders;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.langs.util.TaskExecutor;
import fi.helsinki.cs.tmc.langs.util.TaskExecutorImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the TMC core so other classes can get it when necessary.
 */
public class TmcCoreHolder {

    private static final Logger logger = LoggerFactory.getLogger(TmcCoreHolder.class);
    private static TmcCore core;

    private TmcCoreHolder() {}

    public static synchronized TmcCore get() {
        logger.info("Get TmcCore. @TmcCoreHolder.");
        if (core == null) {
            TaskExecutor tmcLangs = new TaskExecutorImpl();
            core = new TmcCore(TmcSettingsManager.get(), tmcLangs);
        }
        return core;
    }

    public static synchronized void setup() {
        logger.info("Setup TmcCore. @TmcCoreHolder.");
        if (core == null) {
            TaskExecutor tmcLangs = new TaskExecutorImpl();
            core = new TmcCore(TmcSettingsManager.get(), tmcLangs);
        }
    }

    //Testing only
    public static synchronized TmcCore testGet() {
        logger.info("Get TmcCore for testing. @TmcCoreHolder");
        if (core == null) {
            TaskExecutor tmcLangs = new TaskExecutorImpl();
            core = new TmcCore(new SettingsTmc("https://tmc.mooc.fi/staging/org/tmc-intellij",
                    "intellij-student", "intellij-tmc"), tmcLangs);
        }
        return core;
    }
}
