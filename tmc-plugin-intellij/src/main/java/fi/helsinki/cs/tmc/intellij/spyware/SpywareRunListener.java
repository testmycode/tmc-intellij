package fi.helsinki.cs.tmc.intellij.spyware;

import com.intellij.execution.ui.RunContentManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class SpywareRunListener  {

    private static final Logger logger = LoggerFactory.getLogger(SpywareRunListener.class);

    public SpywareRunListener(Project project) {
        connectToMessageBus(project);
    }

    private void connectToMessageBus(Project project) {
        logger.info("Connecting to message bus.");
        MessageBusConnection bus = project.getMessageBus().connect();
        bus.setDefaultHandler(new MessageHandler() {
            @Override
            public void handle(Method method, Object... objects) {
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
            }
        });
        logger.info("Subscribing to RunContentManager topic.");
        bus.subscribe(RunContentManager.TOPIC);
    }
}
