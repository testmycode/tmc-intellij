package fi.helsinki.cs.tmc.intellij.snapshots;

import com.google.common.base.Optional;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.communication.TmcServerCommunicationTaskFactory;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.snapshots.*;

import com.intellij.openapi.application.ApplicationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for adding events to the buffer. The buffer then sends and saves the
 * events when necessary.
 */
public class SnapshotsEventManager {

    private static final Logger logger = LoggerFactory.getLogger(SnapshotsEventManager.class);

    private static final EventSendBuffer buffer =
            new EventSendBuffer(new TmcServerCommunicationTaskFactory(), new EventStore());

    public static void add(final LoggableEvent log) {
        ApplicationManager.getApplication()
                .executeOnPooledThread(
                        () -> {
                            try {
                                if (TmcSettingsManager.get().getCurrentCourse().isPresent()
                                        && TmcSettingsManager.get()
                                                        .getCurrentCourse()
                                                        .get()
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
                                                                                    .getCurrentCourse()
                                                                                    .get())
                                                                    .call()));
                                }
                            } catch (Exception e) {
                            }
                            buffer.receiveEvent(log);
                            logger.info("Event has been added to the buffer.");
                        });
    }

    public static EventSendBuffer get() {
        return buffer;
    }
}
