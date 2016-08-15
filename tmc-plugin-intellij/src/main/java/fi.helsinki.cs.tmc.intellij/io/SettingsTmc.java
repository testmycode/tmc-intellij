package fi.helsinki.cs.tmc.intellij.io;

import fi.helsinki.cs.tmc.core.configuration.TmcSettings;
import fi.helsinki.cs.tmc.core.domain.Course;

import com.google.common.base.Optional;

import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;

/**
 * TMC Settings component from Core, has all the necessary settings.
 */
public class SettingsTmc implements TmcSettings, Serializable {

    private String username;
    private String password;
    private String serverAddress;
    private Course course;
    private String projectBasePath;

    private List<URI> spywareUrl;

    public SettingsTmc(String serverAddress, String username, String password) {
        this.serverAddress = serverAddress;
        this.username = username;
        this.password = password;
    }

    /**
     * Sets the default folder for TMC project files -> home/IdeaProjects/TMCProjects .
     */
    public SettingsTmc() {
        JFileChooser fileChooser = new JFileChooser();
        serverAddress = "https://tmc.mooc.fi/staging/org/tmc-intellij/";
        projectBasePath = fileChooser.getFileSystemView().getDefaultDirectory().toString()
                + File.separator + "IdeaProjects" + File.separator + "TMCProjects";
    }

    public List<URI> getSpywareUrl() {
        return spywareUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getProjectBasePath() {
        return projectBasePath;
    }

    public void setProjectBasePath(String projectBasePath) {
        if (projectBasePath.contains("TMCProjects")) {
            this.projectBasePath = projectBasePath;
        } else {
            this.projectBasePath = projectBasePath + File.separator + "TMCProjects";
        }
    }

    @Override
    public String getServerAddress() {
        return serverAddress;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean userDataExists() {
        return this.username != null && this.password != null
                && !this.username.isEmpty() && !this.password.isEmpty();
    }

    @Override
    public Optional<Course> getCurrentCourse() {
        Optional<Course> crs = Optional.of(course);
        return crs;
    }

    @Override
    public String apiVersion() {
        return "7";
    }

    @Override
    public String clientName() {
        return "idea_plugin";
    }

    @Override
    public String clientVersion() {
        return "0.5.0";
    }

    @Override
    public String getFormattedUserData() {
        return null;
    }

    @Override
    public Path getTmcProjectDirectory() {
        return Paths.get(projectBasePath);
    }

    @Override
    public Locale getLocale() {
        Locale locale = new Locale("en");
        return locale;
    }

    @Override
    public SystemDefaultRoutePlanner proxy() {
        return null;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public void setConfigRoot(Path path) {

    }

    @Override
    public Path getConfigRoot() {
        JFileChooser fileChooser = new JFileChooser();
        return Paths.get(fileChooser.getFileSystemView().getDefaultDirectory().toString());
    }
}
