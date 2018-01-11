package fi.helsinki.cs.tmc.intellij.spyware;

import com.google.common.base.Optional;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.communication.TmcServerCommunicationTaskFactory;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.spyware.EventSendBuffer;
import fi.helsinki.cs.tmc.spyware.EventStore;
import fi.helsinki.cs.tmc.spyware.LoggableEvent;
import fi.helsinki.cs.tmc.spyware.SpywareSettings;

import com.intellij.openapi.application.ApplicationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for adding events to the buffer. The buffer then sends and saves the
 * events when necessary.
 */
public class SpywareEventManager {

    private static final Logger logger = LoggerFactory.getLogger(SpywareEventManager.class);

    private static final SpywareSettings spywareSettings =
            new SpywareSettings() {
                @Override
                public boolean isSpywareEnabled() {
                    return true;
                }

                @Override
                public boolean isDetailedSpywareEnabled() {
                    return true;
                }
            };

    private static final EventSendBuffer buffer =
            new EventSendBuffer(
                    spywareSettings, new TmcServerCommunicationTaskFactory(), new EventStore());

    public static void add(final LoggableEvent log) {
        if (!spywareIsActivated()) {
            return;
        }

        ApplicationManager.getApplication()
                .executeOnPooledThread(
                        () -> {
                            try {
                                if (TmcSettingsManager.get().getCurrentCourse().isPresent()
                                        && TmcSettingsManager.get()
                                                        .getCurrentCourse().get()
                                                        .getSpywareUrls()
                                                        .size()
                                                == 0) {

                                    logger.info("Trying to get course info.");
                                    TmcCore core = TmcCoreHolder.get();
                                    TmcSettingsManager.get()
                                            .setCourse(
                                                    Optional.of(
                                                            core.getCourseDetails(
                                                                            ProgressObserver
                                                                                    .NULL_OBSERVER,
                                                                            TmcSettingsManager.get()
                                                                                    .getCurrentCourse().get())
                                                                    .call()));
                                }
                            } catch (Exception e) {
                            }
                            buffer.receiveEvent(log);
                            logger.info("Event has been added to the buffer.");
                        });
    }

    private static boolean spywareIsActivated() {
        return TmcSettingsManager.get().isSpyware();
    }

    public static EventSendBuffer get() {
        return buffer;
    }
}
