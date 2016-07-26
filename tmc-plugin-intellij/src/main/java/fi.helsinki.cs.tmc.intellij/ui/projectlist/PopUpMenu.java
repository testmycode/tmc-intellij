package fi.helsinki.cs.tmc.intellij.ui.projectlist;

import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;

public class PopUpMenu extends JBPopupMenu {

    public PopUpMenu() {
    }

    public void addItemToMenu(JBMenuItem item) {
        add(item);
    }
}
