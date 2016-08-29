package fi.helsinki.cs.tmc.intellij.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import fi.helsinki.cs.tmc.core.domain.Course;

import org.junit.Before;
import org.junit.Test;

import javax.swing.JFileChooser;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


public class SettingsTmcTest {

    private SettingsTmc settingstmc;

    @Before
    public void setUp() throws Exception {
        settingstmc = new SettingsTmc();
    }

    @Test
    public void setUsernameWorksCorrectly() throws Exception {
        settingstmc.setUsername("koira");
        String user = settingstmc.getUsername();
        assertEquals("koira", user);
    }

    @Test
    public void setPasswordWorksCorrectly() throws Exception {
        settingstmc.setPassword("koira");
        String user = settingstmc.getPassword();
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
        user = settingstmc.getUsername();
        assertEquals("koira" + File.separator + "TMCProjects", user);
    }

    @Test
    public void userDataExistsReturnsTrue() throws Exception {
        settingstmc.setUsername("koira");
        settingstmc.setPassword("kissa");
        Boolean bool = settingstmc.userDataExists();
        assertTrue(bool);
    }

    @Test
    public void userDataExistsReturnsFalse() throws Exception {
        settingstmc.setUsername("koira");
        Boolean bool = settingstmc.userDataExists();
        assertFalse(bool);
    }

    @Test
    public void apiVersionWorksCorrectly() throws Exception {
        assertEquals("7", settingstmc.apiVersion());
    }

    @Test
    public void clientNameWorksCorrectly() throws Exception {
        assertEquals("idea_plugin", settingstmc.clientName());
    }

    @Test
    public void getFormattedUserDataWorksCorrectly() throws Exception {
        assertEquals(null, settingstmc.getFormattedUserData());
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
        settingstmc.setCourse(course);
        Course course1 = settingstmc.getCourse();
        assertEquals(course, course1);
    }

    @Test
    public void getConfigRootWorksCorrectly() throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        Path path = Paths.get(fileChooser.getFileSystemView().getDefaultDirectory().toString());
        assertEquals(path, settingstmc.getConfigRoot());
    }


}