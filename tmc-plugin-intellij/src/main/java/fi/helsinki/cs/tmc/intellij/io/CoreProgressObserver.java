package fi.helsinki.cs.tmc.intellij.io;

import fi.helsinki.cs.tmc.core.domain.ProgressObserver;

import com.intellij.openapi.progress.util.ProgressWindow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreProgressObserver extends ProgressObserver {

    private final ProgressWindow progressWindow;
    private static final Logger logger = LoggerFactory.getLogger(CoreProgressObserver.class);

    public CoreProgressObserver(ProgressWindow progressWindow) {
        this.progressWindow = progressWindow;
    }

    @Override
    public void progress(long mysteryLong, String status) {
        logger.info("Setting progress status. @CoreProgressObserver");
        progressWindow.setText2(status);
        progressWindow.checkCanceled();
    }

    @Override
    public void progress(long mysteryLong, Double progress, String status) {
        logger.info("Setting progress status. @CoreProgressObserver");
        progressWindow.setText2(status);
        progressWindow.setFraction(progress);
        progressWindow.checkCanceled();
    }

    @Override
    public void start(long mysteryLong) {
        logger.info("Opening progress window. @CoreProgressObserver");
        progressWindow.start();
    }

    @Override
    public void end(long mysteryLong) {
        logger.info("Closing progress window. @CoreProgressObserver");
        progressWindow.dispose();
    }
}
