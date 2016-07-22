package fi.helsinki.cs.tmc.intellij.ui;


import fi.helsinki.cs.tmc.intellij.ui.elements.SettingsPanel;

import javax.swing.*;
import java.awt.*;

public class SettingsWindow {

    JFrame frame;


    public SettingsWindow(){
        frame = new JFrame();
        JPanel panel = new SettingsPanel(frame).getPanel();
        frame.setSize(new Dimension(700, 400));
        frame.add(panel);
        frame.setTitle("TMC Settings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(new Dimension(700, 400));
    }

}
