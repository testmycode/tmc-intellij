package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.application.ex.ClipboardUtil;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Offers method for copying text to clip board.
 */
public class ClipboardService {

    public static void copyToClipBoard(String stringToCopy) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection selection = new StringSelection(stringToCopy);
        clipboard.setContents(selection, null);
    }

    public static String getClipBoard() {
        return ClipboardUtil.getTextInClipboard();
    }
}
