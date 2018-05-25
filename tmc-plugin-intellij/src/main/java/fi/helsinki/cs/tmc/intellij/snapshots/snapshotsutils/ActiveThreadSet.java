package fi.helsinki.cs.tmc.intellij.snapshots.snapshotsutils;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A set of active or unstarted threads.
 *
 * <p>Note that this is not a thread group. If a thread T1 in a thread set spawns another thread T2
 * then T2 will <em>not</em> be in the thread set.
 */
public class ActiveThreadSet {
    private final LinkedList<Thread> threads;

    public ActiveThreadSet() {
        this.threads = new LinkedList<>();
    }

    public void addThread(Thread thread) {
        cleanUp();
        threads.add(thread);
    }

    /** Waits for all threads to terminate. */
    public void joinAll() throws InterruptedException {
        while (!threads.isEmpty()) {
            Thread thread = cleanUpToFirstUnterminated();
            if (thread != null) {
                thread.join();
            }
        }
    }

    private void cleanUp() {
        threads.removeIf(thread -> thread.getState() == Thread.State.TERMINATED);
    }

    private Thread cleanUpToFirstUnterminated() {
        Iterator<Thread> iterator = threads.iterator();
        while (iterator.hasNext()) {
            Thread thread = iterator.next();
            if (thread.getState() == Thread.State.TERMINATED) {
                iterator.remove();
            } else {
                return thread;
            }
        }
        return null;
    }
}
