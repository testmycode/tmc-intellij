package fi.helsinki.cs.tmc.intellij.io;

import fi.helsinki.cs.tmc.core.domain.ProgressObserver;

import com.intellij.openapi.progress.util.ProgressWindow;

public class CoreProgressObserver extends ProgressObserver{

    private final ProgressWindow progressWindow;

    public CoreProgressObserver(ProgressWindow progressWindow) {
        this.progressWindow = progressWindow;
    }

    @Override
    public void progress(long mysteryLong, String status) {
        progressWindow.setText2(status);
        progressWindow.checkCanceled();
    }

    @Override
    public void progress(long mysteryLong, Double progress, String status) {
        progressWindow.setText2(status);
        progressWindow.setFraction(progress);
        progressWindow.checkCanceled();
    }

    @Override
    public void start(long mysteryLong) {
        progressWindow.start();
    }

    @Override
    public void end(long mysteryLong) {
        progressWindow.dispose();
    }
}
