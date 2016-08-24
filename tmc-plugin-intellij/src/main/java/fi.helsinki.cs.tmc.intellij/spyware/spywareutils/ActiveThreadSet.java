package fi.helsinki.cs.tmc.intellij.spyware.spywareutils;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A set of active or unstarted threads.
 *
 * <p>
 * Note that this is not a thread group. If a thread T1 in a thread set
 * spawns another thread T2 then T2 will <em>not</em> be in the thread set.
 */
public class ActiveThreadSet {
    private LinkedList<Thread> threads;

    public ActiveThreadSet() {
        this.threads = new LinkedList<Thread>();
    }

    public void addThread(Thread thread) {
        cleanUp();
        threads.add(thread);
    }

    /**
     * Waits for all threads to terminate.
     */
    public void joinAll() throws InterruptedException {
        while (!threads.isEmpty()) {
            Thread thread = cleanUpToFirstUnterminated();
            if (thread != null) {
                thread.join();
            }
        }
    }

    private void cleanUp() {
        Iterator<Thread> i = threads.iterator();
        while (i.hasNext()) {
            Thread t = i.next();
            if (t.getState() == Thread.State.TERMINATED) {
                i.remove();
            }
        }
    }

    private Thread cleanUpToFirstUnterminated() {
        Iterator<Thread> i = threads.iterator();
        while (i.hasNext()) {
            Thread t = i.next();
            if (t.getState() == Thread.State.TERMINATED) {
                i.remove();
            } else {
                return t;
            }
        }
        return null;
    }

}