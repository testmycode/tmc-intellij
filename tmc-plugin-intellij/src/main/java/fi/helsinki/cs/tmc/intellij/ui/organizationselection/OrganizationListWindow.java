package fi.helsinki.cs.tmc.intellij.ui.organizationselection;

import com.google.common.base.Optional;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import fi.helsinki.cs.tmc.core.domain.Organization;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.holders.TmcSettingsHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.login.LoginManager;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;
import fi.helsinki.cs.tmc.intellij.ui.courseselection.CourseListWindow;
import fi.helsinki.cs.tmc.intellij.ui.settings.SettingsPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.List;

public class OrganizationListWindow extends JPanel {

    private static JFrame frame;
    private final JList<OrganizationCard> organizations;
    private static JButton button;

    private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);

    public OrganizationListWindow(List<Organization> organizations) {
        OrganizationCard[] organizationCards = new OrganizationCard[organizations.size()];
        Collections.sort(organizations, (a, b) -> {
            if (a.isPinned() && b.isPinned()) {
                return a.getName().compareTo(b.getName());
            }
            if (a.isPinned()) {
                return -1;
            }
            if (b.isPinned()) {
                return 1;
            }
            return a.getName().compareTo(b.getName());
        });
        for (int i = 0; i < organizations.size(); i++) {
            organizationCards[i] = new OrganizationCard(organizations.get(i));
        }
        this.organizations = new JBList<>(organizationCards);

        this.organizations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.button = new JButton("Select");
        button.addActionListener(new SelectOrganizationListener(this));

        this.organizations.setCellRenderer(new OrganizationCellRenderer());
        this.organizations.setVisibleRowCount(4);
        JScrollPane pane = new JBScrollPane(this.organizations);
        Dimension d = pane.getPreferredSize();
        d.width = 1000;
        d.height = 600;
        pane.setPreferredSize(d);
        pane.setBorder(new EmptyBorder(5, 0, 5, 0));
        this.organizations.setBackground(new Color(242, 241, 240));

        this.organizations.setSelectedIndex(setDefaultSelectedIndex());

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

    public static void display() throws Exception {
        logger.info("Showing OrganizationList. @OrganizationListWindow");

        if (frame == null) {
            frame = new JFrame("Select an organization");
        }
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        List<Organization> organizations =
                TmcCoreHolder.get().getOrganizations(ProgressObserver.NULL_OBSERVER).call();

        final OrganizationListWindow organizationListWindow =
                new OrganizationListWindow(organizations);

        frame.setContentPane(organizationListWindow);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        button.setMinimumSize(new Dimension(organizationListWindow.getWidth(), button.getHeight()));
        button.setMaximumSize(new Dimension(organizationListWindow.getWidth(), button.getHeight()));

        organizationListWindow.addComponentListener(
                new ComponentListener() {
                    @Override
                    public void componentResized(ComponentEvent event) {
                        button.setMinimumSize(
                                new Dimension(
                                        organizationListWindow.getWidth(), button.getHeight()));
                        button.setMaximumSize(
                                new Dimension(
                                        organizationListWindow.getWidth(), button.getHeight()));
                    }

                    @Override
                    public void componentMoved(ComponentEvent e) {}

                    @Override
                    public void componentShown(ComponentEvent e) {}

                    @Override
                    public void componentHidden(ComponentEvent e) {}
                });
    }

    public static boolean isWindowVisible() {
        if (frame == null) {
            return false;
        }
        return frame.isVisible();
    }

    private int setDefaultSelectedIndex() {
        Optional<Organization> selectedOrganization = TmcSettingsHolder.get().getOrganization();
        if (!selectedOrganization.isPresent()) {
            return 0;
        }
        final ListModel<OrganizationCard> list = organizations.getModel();
        for (int i = 0; i < list.getSize(); i++) {
            if (list.getElementAt(i)
                    .getOrganization()
                    .getName()
                    .equals(selectedOrganization.get().getName())) {
                return i;
            }
        }
        return 0;
    }

    class SelectOrganizationListener implements ActionListener {

        private final Logger logger = LoggerFactory.getLogger(LoginManager.class);

        public SelectOrganizationListener(OrganizationListWindow window) {}

        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("Action SelectOrganization performed. @SelectOrganizationListener");

            final OrganizationCard organization = organizations.getSelectedValue();
            setColors(organization, Color.blue, Color.black);
            frame.setVisible(false);
            frame.dispose();

            try {
                final PersistentTmcSettings persistentSettings =
                        ServiceManager.getService(PersistentTmcSettings.class);
                SettingsTmc settingsTmc =
                        ServiceManager.getService(PersistentTmcSettings.class).getSettingsTmc();

                settingsTmc.setOrganization(Optional.of(organization.getOrganization()));
                persistentSettings.setSettingsTmc(settingsTmc);

                if (SettingsPanel.getInstance() != null) {
                    SettingsPanel.getInstance()
                            .setCurrentOrganization(); // update settingspanel if it's visible
                }

                CourseListWindow.display(); // show courselistwindow after selecting an organization
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setColors(OrganizationCard organization, Color background, Color foreground) {
        organization.setBackground(background);
        for (Component c : organization.getComponents()) {
            c.setForeground(foreground);
        }
    }
}

class OrganizationCellRenderer extends JLabel implements ListCellRenderer {

    private static final Color HIGHLIGHT_COLOR = new Color(240, 119, 70);

    public OrganizationCellRenderer() {}

    @Override
    public Component getListCellRendererComponent(
            final JList list,
            final Object value,
            final int index,
            final boolean isSelected,
            final boolean hasFocus) {
        OrganizationCard organization = (OrganizationCard) value;
        ((OrganizationCard) value).setSize(((OrganizationCard) value).getWidth(), 500);
        if (isSelected) {
            organization.setColors(Color.white, HIGHLIGHT_COLOR);
        } else {
            organization.setColors(new Color(76, 76, 76), Color.white);
        }
        return organization;
    }
}
