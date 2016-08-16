package fi.helsinki.cs.tmc.intellij.ui.submissionresult.feedback;

import java.awt.Component;
import javax.swing.Box;


public class Boxer {
    public static Component hglue() {
        return Box.createHorizontalGlue();
    }

    public static Component vglue() {
        return Box.createVerticalGlue();
    }

    public static Component hstrut(int width) {
        return Box.createHorizontalStrut(width);
    }

    public static Component vstrut(int height) {
        return Box.createVerticalStrut(height);
    }

    public static Box hbox(Component... components) {
        Box box = Box.createHorizontalBox();

        addComponentsToBox(box, components);
        return box;
    }

    public static Box vbox(Component... components) {
        Box box = Box.createVerticalBox();

        addComponentsToBox(box, components);
        return box;
    }

    private static void addComponentsToBox(Box box, Component... components) {
        for (Component component : components) {
            box.add(component);
        }
    }
}
