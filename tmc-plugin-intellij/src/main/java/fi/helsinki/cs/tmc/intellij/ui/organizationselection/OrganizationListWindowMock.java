package fi.helsinki.cs.tmc.intellij.ui.organizationselection;

import com.intellij.ui.components.JBScrollPane;
import fi.helsinki.cs.tmc.core.domain.Organization;
import fi.helsinki.cs.tmc.intellij.services.login.LoginManager;
import fi.helsinki.cs.tmc.intellij.ui.settings.SettingsPanelMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class OrganizationListWindowMock extends JPanel {

    private final JList<Organization> organizations;
    private static JButton button;

    private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);

    public OrganizationListWindowMock(List<Organization> organizations) {
        Organization[] orgArray = organizations.toArray(new Organization[organizations.size()]);
        this.organizations = new JList<Organization>(orgArray);

        this.button = new JButton("Select");
        button.addActionListener(new SelectOrganizationListenerMock(this));

        JScrollPane pane = new JBScrollPane(this.organizations);

        this.organizations.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.getClickCount() >= 2) {
                            button.doClick();
                        }
                    }
                });

        add(pane);
        add(button);
    }

    public JList<Organization> getOrganizations() {
        return this.organizations;
    }

    public JButton getButton() {
        return button;
    }

    class SelectOrganizationListenerMock implements ActionListener {

        private final Logger logger = LoggerFactory.getLogger(LoginManager.class);

        public SelectOrganizationListenerMock(OrganizationListWindowMock window) {}

        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("Action SelectOrganization performed. @SelectOrganizationListener");

            final Organization organization = organizations.getSelectedValue();



            try {
                if (SettingsPanelMock.getInstance() != null) {
                    SettingsPanelMock.getInstance()
                            .setCurrentOrganization(
                                    organization); // update settingspanel if it's visible
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
