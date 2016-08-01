package fi.helsinki.cs.tmc.intellij.services;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class ClipboardService {

    public static void copyToClipBoard(String stringToCopy) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection selection = new StringSelection(stringToCopy);
        clipboard.setContents(selection, null);
    }
}
