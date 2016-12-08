package fi.helsinki.cs.tmc.intellij.ui.testresults;

import com.intellij.ui.JBProgressBar;
import junit.framework.Test;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

public class TestResultProgressBar extends JBProgressBar {
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
            int height = getHeight();
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
                    graphic.setColor(TestResultColors.TEST_BORDER_SUCCESS);
                } else {
                    graphic.setColor(TestResultColors.TEST_BORDER_VALIDATION);
                }
                graphic.fillRect(0, 0, filled, height);
                graphic.setColor(TestResultColors.TEST_BORDER_FAIL);
                graphic.fillRect(filled, 0, notFilled, height);

                if (isStringPainted()) {
                    graphic.setColor(Color.BLACK);
                    String string = getString();
                    FontMetrics fm = graphic.getFontMetrics();
                    Rectangle textBox = fm.getStringBounds(string, graphic).getBounds();

                    int midX = width / 2;
                    int midY = height / 2;
                    int textX = midX - textBox.width / 2;
                    int textY = midY + textBox.height / 2 - fm.getDescent();

                    graphic.drawString(string, textX, textY);
                }
            } else {
                // Is the bar ever used indeterminate?
                graphic.setColor(Color.WHITE);
                graphic.fillRect(0, 0, width, height);
            }

        } finally {
            graphic.setColor(oldColor);
        }
    }
}
