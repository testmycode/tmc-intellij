package fi.helsinki.cs.tmc.intellij.spyware;


import fi.helsinki.cs.tmc.core.communication.TmcServerCommunicationTaskFactory;
import fi.helsinki.cs.tmc.core.spyware.EventSendBuffer;
import fi.helsinki.cs.tmc.core.spyware.EventStore;
import fi.helsinki.cs.tmc.core.spyware.LoggableEvent;
import fi.helsinki.cs.tmc.core.spyware.SpywareSettings;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;

import java.util.List;

public class SpywareEventManager {

    private static SpywareSettings spywareSettings = new SpywareSettings() {
        @Override
        public boolean isSpywareEnabled() {
            return true;
        }

        @Override
        public boolean isDetailedSpywareEnabled() {
            return true;
        }
    };
    private static EventSendBuffer buffer = new EventSendBuffer(spywareSettings, new TmcServerCommunicationTaskFactory(), new EventStore());

    public static void add(LoggableEvent log) {
        buffer.receiveEvent(log);
    }

    public static EventSendBuffer get() {
        return buffer;
    }
}
