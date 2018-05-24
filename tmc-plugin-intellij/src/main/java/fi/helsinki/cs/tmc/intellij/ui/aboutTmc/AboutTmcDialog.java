package fi.helsinki.cs.tmc.intellij.ui.aboutTmc;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.components.JBScrollPane;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

public class AboutTmcDialog extends JDialog {
    private JButton closeButton;
    private JLabel headerLabel;
    private JScrollPane infoScrollPane;
    private JTextPane infoTextPane;

    public static void display() {
        AboutTmcDialog dialog = new AboutTmcDialog();

        dialog.setLocationRelativeTo(null);
        dialog.setTitle("About");
        dialog.setVisible(true);
        dialog.setAlwaysOnTop(true);
    }

    public AboutTmcDialog() {
        super(
                WindowManager.getInstance()
                        .getFrame(ProjectManager.getInstance().getDefaultProject()),
                false);
        initComponents();

        this.headerLabel.setText("About Test My Code");
        this.infoTextPane.setText("<html> <body> <div> Test My Code is a service designed for learning and teaching programming. It is open "
                        + "source, provides support for automatic assessment of programming assignments, and comes with a server "
                        + "that can be used to maintain course-specific point lists and grading. </div> <br> <div> "
                        + "Find out more at <a href=\"https://mooc.fi/tmc\">https://mooc.fi/tmc</a>. </div> </body> </html>");
        this.closeButton.setText("Close");

        this.infoTextPane.addHyperlinkListener((HyperlinkEvent e) -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(new URI("https://mooc.fi/tmc"));
                } catch (Exception ex) {
                    new ErrorMessageService().showErrorMessagePopup("Failed to open browser.\n" + ex.getMessage());
                }
            }
        });
    }

    private void initComponents() {
        headerLabel = new JLabel();
        closeButton = new JButton();
        infoScrollPane = new JBScrollPane();
        infoTextPane = new JTextPane();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMaximumSize(new Dimension(466, 280));
        setMinimumSize(new Dimension(466, 280));
        setResizable(false);

        headerLabel.setFont(new Font("Ubuntu", 1, 20));

        closeButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        closeButtonActionPerformed(e);
                    }
                });

        infoTextPane.setEditable(false);
        infoTextPane.setContentType("text/html");

        infoScrollPane.setViewportView(infoTextPane);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment
                                                                        .LEADING)
                                                        .addComponent(
                                                                infoScrollPane,
                                                                javax.swing.GroupLayout
                                                                        .PREFERRED_SIZE,
                                                                0,
                                                                Short.MAX_VALUE)
                                                        .addGroup(
                                                                javax.swing.GroupLayout.Alignment
                                                                        .TRAILING,
                                                                layout.createSequentialGroup()
                                                                        .addGap(
                                                                                0,
                                                                                0,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(closeButton))
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addComponent(headerLabel)
                                                                        .addGap(
                                                                                0,
                                                                                248,
                                                                                Short.MAX_VALUE)))
                                        .addContainerGap()));

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(headerLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(infoScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(closeButton)
                    .addContainerGap())
        );

        pack();
    }

    private void closeButtonActionPerformed(ActionEvent e) {
        this.setVisible(false);
        this.dispose();
    }
}
