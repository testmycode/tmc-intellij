package fi.helsinki.cs.tmc.intellij.snapshots;

import com.intellij.execution.ui.RunContentManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnapshotsRunListener {

    private static final Logger logger = LoggerFactory.getLogger(SnapshotsRunListener.class);

    public SnapshotsRunListener(Project project) {
        connectToMessageBus(project);
    }

    private void connectToMessageBus(Project project) {
        logger.info("Connecting to message bus.");
        MessageBusConnection bus = project.getMessageBus().connect();
        bus.setDefaultHandler(
                (method, objects) -> {
                    logger.info("Method call observed in message bus.");
                    for (Object object : objects) {
                        if (method.toString().toLowerCase().contains("contentselected")) {
                            if (object.toString().toLowerCase().contains("debug")) {
                                new ButtonInputListener().receiveDebugRunAction();
                            } else if (object.toString().contains("DefaultRunExecutor")) {
                                new ButtonInputListener().receiveRunAction();
                            }
                        }
                    }
                });
        logger.info("Subscribing to RunContentManager topic.");
        bus.subscribe(RunContentManager.TOPIC);
    }
}
