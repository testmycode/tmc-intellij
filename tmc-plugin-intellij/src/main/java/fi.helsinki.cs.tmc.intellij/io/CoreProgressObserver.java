package fi.helsinki.cs.tmc.intellij.io;

import fi.helsinki.cs.tmc.core.domain.ProgressObserver;

public class CoreProgressObserver extends ProgressObserver {
    @Override
    public void progress(long l, String s) {
        System.out.println(s);
    }

    @Override
    public void progress(long l, Double aDouble, String s) {
        System.out.println(s);
    }

    @Override
    public void start(long l) {

    }

    @Override
    public void end(long l) {

    }
}
