package fi.helsinki.cs.tmc.intellij.ui.organizationselection;

import fi.helsinki.cs.tmc.core.domain.Organization;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class OrganizationListWindowTest {
    private OrganizationListWindow orgListWin;

    @Before
    public void setUp() throws Exception {
        Organization org1 = new Organization("Default", "default", "default", "default", false);
        Organization org2 = new Organization("Kissa", "Kissa", "kissa", "kissa", false);
        Organization org3 = new Organization("Testi", "Testaukseen", "test", "test", false);
        List<Organization> orgs = new ArrayList<>();
        orgs.add(org1);
        orgs.add(org2);
        orgs.add(org3);

        orgListWin = new OrganizationListWindow(orgs);
    }

    @Test
    public void containsButton() {
        boolean isButton = false;

        for (Component component : orgListWin.getComponents()) {
            if (component.getClass() == JButton.class) {
                isButton = true;
            }
        }
        assertTrue(isButton);
    }
}
