package fi.helsinki.cs.tmc.intellij.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Optional;
import fi.helsinki.cs.tmc.core.domain.Course;

import fi.helsinki.cs.tmc.core.domain.OauthCredentials;
import fi.helsinki.cs.tmc.core.domain.Organization;
import org.junit.Before;
import org.junit.Test;

import javax.swing.JFileChooser;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public class SettingsTmcTest {

    private SettingsTmc settingstmc;

    @Before
    public void setUp() throws Exception {
        settingstmc = new SettingsTmc();
    }

    @Test
    public void setUsernameWorksCorrectly() throws Exception {
        settingstmc.setUsername("koira");
        String user = settingstmc.getUsername().get();
        assertEquals("koira", user);
    }

    @Test
    public void setPasswordWorksCorrectly() throws Exception {
        settingstmc.setPassword("koira");
        String user = settingstmc.getPassword().get();
        assertEquals("koira", user);
    }

    @Test
    public void setServerAddressWorksCorrectly() throws Exception {
        settingstmc.setServerAddress("koira");
        String user = settingstmc.getServerAddress();
        assertEquals("koira", user);
    }

    @Test
    public void setProjectBasePathWorksCorrectly() throws Exception {
        settingstmc.setProjectBasePath("koira");
        String user = settingstmc.getProjectBasePath();
        assertEquals("koira" + File.separator + "TMCProjects", user);
        settingstmc.setUsername("koira" + File.separator + "TMCProjects");
        user = settingstmc.getUsername().get();
        assertEquals("koira" + File.separator + "TMCProjects", user);
    }

    @Test
    public void userDataExistsReturnsTrueWhenUsernameAndPasswordExist() throws Exception {
        settingstmc.setUsername("koira");
        settingstmc.setPassword("kissa");
        Boolean bool = settingstmc.userDataExists();
        assertTrue(bool);
    }

    @Test
    public void userDataExistsReturnsFalseWhenPassWordDoesNotExist() throws Exception {
        settingstmc.setUsername("koira");
        Boolean bool = settingstmc.userDataExists();
        assertFalse(bool);
    }

    @Test
    public void setOrganizationWorksCorrectly() {
        settingstmc.setOrganization(Optional.of(new Organization("Default", "default", "default", "default", false)));
        assertEquals("Default", settingstmc.getOrganization().get().getName());
        assertEquals("default", settingstmc.getOrganization().get().getInformation());
    }

    @Test
    public void setOauthCredentialsWorksCorrectly() {
        settingstmc.setOauthCredentials(Optional.of(new OauthCredentials("kissa", "koira")));
        assertEquals("kissa", settingstmc.getOauthCredentials().get().getOauthApplicationId());
    }

    @Test
    public void setTokenWorksCorrectly() {
        settingstmc.setToken(Optional.of("kissa"));
        assertEquals("kissa", settingstmc.getToken().get());
    }

    @Test
    public void clientNameWorksCorrectly() throws Exception {
        assertEquals("idea_plugin", settingstmc.clientName());
    }
    @Test
    public void getTmcProjectDirectoryWorksCorrectly() throws Exception {
        Path path = Paths.get("koira" + File.separator + "TMCProjects");
        settingstmc.setProjectBasePath(path.toString());
        assertEquals(path, settingstmc.getTmcProjectDirectory());
    }

    @Test
    public void proxyWorksCorrectly() throws Exception {
        assertEquals(null, settingstmc.proxy());
    }

    @Test
    public void setCourseWorksCorrectly() throws Exception {
        Course course = new Course();
        settingstmc.setCourse(Optional.of(course));
        Course course1 = settingstmc.getCurrentCourse().get();
        assertEquals(course, course1);
    }

    @Test
    public void getConfigRootWorksCorrectly() throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        Path path = Paths.get(fileChooser.getFileSystemView().getDefaultDirectory().toString());
        assertEquals(path, settingstmc.getConfigRoot());
    }


}
