package fi.helsinki.cs.tmc.intellij.ui.testresults;

import com.intellij.ui.JBProgressBar;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Created by melchan on 1.9.2016.
 */
public class TestResultProgressBar extends JBProgressBar {
    private static final Color PASS_COLOR = new Color(0x00C800);
    private static final Color FAIL_COLOR = new Color(0xE10000);
    private static final Color VALIDATION_COLOR = new Color(0xFFD000);
    private static final Color UNSET_COLOR = new Color(0xEEEEEE);

    private boolean validationPass;

    public TestResultProgressBar() {
        super();
        validationPass = true;
        setStringPainted(true);
    }

    public void validationPass(final boolean validationPassed) {

        validationPass = validationPassed;
    }

    @Override
    public String getString() {
        if (getMaximum() - getMinimum() > 0) {
            return super.getString();
        } else {
            return "(no tests)";
        }
    }

    @Override
    protected void paintComponent(Graphics graphic) {

        Color oldColor = graphic.getColor();

        try {
            int width = getWidth();
            int height = getHeight() / 2;
            graphic.clearRect(0, 0, width, height);

            if (!isIndeterminate()) {
                int range = (getMaximum() - getMinimum());
                int filled;
                if (range > 0) {
                    filled = width * getValue() / range;
                } else {
                    filled = width;
                }
                int notFilled = width - filled;
                if (validationPass) {
                    graphic.setColor(PASS_COLOR);
                } else {
                    graphic.setColor(VALIDATION_COLOR);
                }
                graphic.fillRect(0, 0, filled, height);
                graphic.setColor(FAIL_COLOR);
                graphic.fillRect(filled, 0, notFilled, height);

                if (isStringPainted()) {
                    graphic.setColor(Color.BLACK);
                    String s = getString();
                    FontMetrics fm = graphic.getFontMetrics();
                    Rectangle textBox = fm.getStringBounds(s, graphic).getBounds();

                    int midX = width / 2;
                    int midY = height / 2;
                    int textX = midX - textBox.width / 2;
                    int textY = midY + textBox.height / 2 - fm.getDescent();

                    graphic.drawString(s, textX, textY);
                }
            } else {
                graphic.setColor(UNSET_COLOR);
                graphic.fillRect(0, 0, width, height);
            }

        } finally {
            graphic.setColor(oldColor);
        }
    }
}
