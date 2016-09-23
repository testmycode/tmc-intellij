package fi.helsinki.cs.tmc.intellij.ui.pastebin;

import fi.helsinki.cs.tmc.intellij.services.PasteService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** Controls the pastebin. */
public class PasteWindow {

    private static final Logger logger = LoggerFactory.getLogger(PasteWindow.class);
    private JFrame frame;

    public void showSubmit(PasteService pasteService) {
        logger.info("Showing paste submit window. @PasteWindow");
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
        logger.info("Showing paste results window. @PasteWindow");
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
        logger.info("Closing paste window. @PasteWindow");
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public boolean isClosed() {
        return (frame == null || !frame.isVisible());
    }

    public void show() {
        logger.info("Showing paste window. @PasteWindow");
        frame.setVisible(false);
        frame.setVisible(true);
    }
}
