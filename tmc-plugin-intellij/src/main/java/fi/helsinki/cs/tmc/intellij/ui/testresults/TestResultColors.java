package fi.helsinki.cs.tmc.intellij.ui.testresults;

import com.intellij.ui.JBColor;

import java.awt.Color;

public class TestResultColors {
    // new JBColor(light color, dark color);
    public static final JBColor TEST_BORDER_SUCCESS = new JBColor(0x6FD06D, 0x6FD06D);
    public static final JBColor TEST_BORDER_FAIL = new JBColor(0xED0000, 0xED0000);
    public static final JBColor TEST_BORDER_VALIDATION =  new JBColor(0xFFD000, 0xFFD000);

    public static final JBColor TEST_TITLE_SUCCESS = new JBColor(0x4D914C, 0x6FD06D);
    public static final JBColor TEST_TITLE_FAIL = new JBColor(0xA50000, 0Xff6c5c);
    public static final JBColor TEST_TITLE_VALIDATION = new JBColor(0xB59403, 0xFFD000);
}
