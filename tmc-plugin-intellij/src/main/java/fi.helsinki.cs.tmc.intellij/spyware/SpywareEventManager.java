package fi.helsinki.cs.tmc.intellij.spyware;


import com.intellij.openapi.application.ApplicationManager;
import fi.helsinki.cs.tmc.core.communication.TmcServerCommunicationTaskFactory;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.spyware.EventSendBuffer;
import fi.helsinki.cs.tmc.core.spyware.LoggableEvent;
import fi.helsinki.cs.tmc.core.spyware.EventStore;
import fi.helsinki.cs.tmc.core.spyware.SpywareSettings;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.io.CoreProgressObserver;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpywareEventManager {

    private List<LoggableEvent> eventList;
    private static EventStore store = new EventStore();
    private static EventSendBuffer buffer = new EventSendBuffer(new SpywareSettings() {
        @Override
        public boolean isSpywareEnabled() {
            return true;
        }

        @Override
        public boolean isDetailedSpywareEnabled() {
            return true;
        }
    }, new TmcServerCommunicationTaskFactory(), store);

    public static void add(LoggableEvent log) {
        buffer.receiveEvent(log);
        buffer.setSavingInterval(5000);
        buffer.setSendingInterval(30000);
    }

    public static EventSendBuffer get() {
        return buffer;
    }
}
