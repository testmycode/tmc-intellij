package fi.helsinki.cs.tmc.intellij.spyware;


import fi.helsinki.cs.tmc.core.communication.TmcServerCommunicationTaskFactory;
import fi.helsinki.cs.tmc.spyware.EventSendBuffer;
import fi.helsinki.cs.tmc.spyware.EventStore;
import fi.helsinki.cs.tmc.spyware.LoggableEvent;
import fi.helsinki.cs.tmc.spyware.SpywareSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for adding events to the buffer.
 * The buffer then sends and saves the events when necessary.
 */

public class SpywareEventManager {

    private static final Logger logger = LoggerFactory.getLogger(SpywareEventManager.class);

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
    private static EventSendBuffer buffer = new EventSendBuffer(spywareSettings,
            new TmcServerCommunicationTaskFactory(), new EventStore());

    public static void add(LoggableEvent log) {
        buffer.receiveEvent(log);
        logger.info("Event has been added to the buffer.");
    }

    public static EventSendBuffer get() {
        return buffer;
    }
}
