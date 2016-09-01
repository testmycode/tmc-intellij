package fi.helsinki.cs.tmc.intellij.ui.testresults;

import com.intellij.ui.JBProgressBar;

import java.awt.*;

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
    protected void paintComponent(Graphics g) {

        Color oldColor = g.getColor();

        try {
            int w = getWidth();
            int h = getHeight() / 2;
            g.clearRect(0, 0, w, h);

            if (!isIndeterminate()) {
                int range = (getMaximum() - getMinimum());
                int filled;
                if (range > 0) {
                    filled = w * getValue() / range;
                } else {
                    filled = w;
                }
                int notFilled = w - filled;
                if (validationPass) {
                    g.setColor(PASS_COLOR);
                } else {
                    g.setColor(VALIDATION_COLOR);
                }
                g.fillRect(0, 0, filled, h);
                g.setColor(FAIL_COLOR);
                g.fillRect(filled, 0, notFilled, h);

                if (isStringPainted()) {
                    g.setColor(Color.BLACK);
                    String s = getString();
                    FontMetrics fm = g.getFontMetrics();
                    Rectangle textBox = fm.getStringBounds(s, g).getBounds();

                    int midX = w / 2;
                    int midY = h / 2;
                    int textX = midX - textBox.width / 2;
                    int textY = midY + textBox.height / 2 - fm.getDescent();

                    g.drawString(s, textX, textY);
                }
            } else {
                g.setColor(UNSET_COLOR);
                g.fillRect(0, 0, w, h);
            }

        } finally {
            g.setColor(oldColor);
        }
    }
}
