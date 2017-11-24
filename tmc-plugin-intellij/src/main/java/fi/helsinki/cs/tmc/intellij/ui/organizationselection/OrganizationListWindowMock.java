package fi.helsinki.cs.tmc.intellij.ui.organizationselection;

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import fi.helsinki.cs.tmc.core.domain.Organization;
import fi.helsinki.cs.tmc.intellij.services.login.LoginManager;
import fi.helsinki.cs.tmc.intellij.ui.courseselection.CourseListWindow;
import fi.helsinki.cs.tmc.intellij.ui.settings.SettingsPanelMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class OrganizationListWindowMock extends JPanel {

    private final JList<OrganizationCard> organizationCards;
    private static JButton button;

    private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);

    public OrganizationListWindowMock(List<Organization> organizations) {
        OrganizationCard[] organizationCards = new OrganizationCard[organizations.size()];

        for (int i = 0; i < organizationCards.length; i++) {
            organizationCards[i] = new OrganizationCard(organizations.get(i));
        }

        this.organizationCards = new JBList<>(organizationCards);

        this.button = new JButton("Select");
        button.addActionListener(new SelectOrganizationListenerMock(this));

        JScrollPane pane = new JBScrollPane(this.organizationCards);

        this.organizationCards.addMouseListener(
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

    public JList<OrganizationCard> getOrganizationCards() {
        return this.organizationCards;
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

            final OrganizationCard organization = organizationCards.getSelectedValue();

            try {
                if (SettingsPanelMock.getInstance() != null) {
                    SettingsPanelMock.getInstance()
                            .setCurrentOrganization(
                                    organization
                                            .getOrganization()); // update settingspanel if it's
                                                                 // visible
                }

                //                CourseListWindow.display(); // show courselistwindow after
                // selecting an organization
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
