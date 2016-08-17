package fi.helsinki.cs.tmc.intellij.ui.submissionresult.feedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Component;
import javax.swing.Box;


public class Boxer {

    private static final Logger logger = LoggerFactory.getLogger(Boxer.class);

    public static Component hglue() {
        logger.info("Creating horizontal glue Box component. @Boxer");
        return Box.createHorizontalGlue();
    }

    public static Box hbox(Component... components) {
        logger.info("Processing hbox. @Boxer");
        Box box = Box.createHorizontalBox();

        addComponentsToBox(box, components);
        return box;
    }

    private static void addComponentsToBox(Box box, Component... components) {
        logger.info("Starting to add components to "
                + "horizontal glue Box component. @Boxer");
        for (Component component : components) {
            logger.info("Adding component " + component
                    + " to horizontal glue Box component. @Boxer");
            box.add(component);
        }
    }
}
