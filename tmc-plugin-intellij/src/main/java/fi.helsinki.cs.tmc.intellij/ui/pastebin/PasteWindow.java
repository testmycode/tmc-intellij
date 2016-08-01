package fi.helsinki.cs.tmc.intellij.ui.pastebin;

import fi.helsinki.cs.tmc.intellij.services.PasteService;

import java.net.URI;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class PasteWindow {

    private JFrame frame;

    public void showSubmit(PasteService pasteService) {
        close();
        frame = new JFrame();
        JPanel panel = new SubmitPanel(pasteService).getPanel();
        frame.add(panel);
        frame.setTitle("TMC Pastebin submit");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(true);
    }

    public void showResult(URI uri) {
        close();
        frame = new JFrame();
        JPanel panel = new ResultPanel(uri, this).getPanel();
        frame.add(panel);
        frame.setTitle("TMC Pastebin result");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(true);
    }

    public void close() {
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public boolean isClosed() {
        return (frame == null || !frame.isVisible());
    }

    public void show() {
        frame.setVisible(false);
        frame.setVisible(true);
    }

}
