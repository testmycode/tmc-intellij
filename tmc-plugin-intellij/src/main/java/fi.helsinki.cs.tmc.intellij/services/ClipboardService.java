package fi.helsinki.cs.tmc.intellij.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Offers method for copying text to clip board.
 */
public class ClipboardService {

    private static final Logger logger = LoggerFactory.getLogger(ClipboardService.class);

    public static void copyToClipBoard(String stringToCopy) {
        logger.info("Copying " + stringToCopy + " to the clip board.");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection selection = new StringSelection(stringToCopy);
        clipboard.setContents(selection, null);
    }
}
