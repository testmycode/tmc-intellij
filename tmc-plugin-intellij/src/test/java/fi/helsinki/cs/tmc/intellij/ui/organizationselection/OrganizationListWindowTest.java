package fi.helsinki.cs.tmc.intellij.ui.organizationselection;

import fi.helsinki.cs.tmc.core.domain.Organization;
import fi.helsinki.cs.tmc.intellij.ui.settings.SettingsPanelMock;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class OrganizationListWindowTest {

    private OrganizationListWindowMock orgListWin;
    private SettingsPanelMock settingsPanel;

    @Before
    public void setUp() {
        createOrganizationListWindow();
        settingsPanel = new SettingsPanelMock(new JFrame());
    }

    public void createOrganizationListWindow() {
        Organization org1 =
                new Organization("Default", "testing intellij plugin", "default", "missing", false);
        Organization org2 =
                new Organization("Kissa", "testing intellij plugin", "kissa", "missing", false);
        Organization org3 =
                new Organization("Testi", "testing intellij plugin", "test", "missing", false);

        List<Organization> organizations = new ArrayList<>();
        organizations.add(org1);
        organizations.add(org2);
        organizations.add(org3);

        orgListWin = new OrganizationListWindowMock(organizations);
    }

    @Test
    public void containsButton() {
        boolean isButton = false;

        for (Component component : orgListWin.getComponents()) {
            if (component.getClass() == JButton.class) {
                isButton = true;
                break;
            }
        }
        assertTrue(isButton);
    }

    @Test
    public void selectingAnOrganizationUpdatesTheLabel() {
        orgListWin.getOrganizations().setSelectedIndex(1);
        orgListWin.getButton().doClick();

        settingsPanel.getOrganizationLabel().getText().contains("Kissa");
    }

    @Test
    public void selectedOrganizationIsSavedInTmcSettings() {
        // TODO: implement if wanted

    }
}
