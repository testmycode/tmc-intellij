package fi.helsinki.cs.tmc.intellij.io;

import fi.helsinki.cs.tmc.core.configuration.TmcSettings;
import fi.helsinki.cs.tmc.core.domain.Course;

import com.google.common.base.Optional;

import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javax.swing.JFileChooser;

/**
 * TMC Settings component from Core, has all the necessary settings.
 */
public class SettingsTmc implements TmcSettings, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(SettingsTmc.class);
    private String username;
    private String password;
    private String serverAddress;
    private Course course;
    private String projectBasePath;

    public SettingsTmc(String serverAddress, String username, String password) {
        this.serverAddress = serverAddress;
        this.username = username;
        this.password = password;
    }

    /**
     * Sets the default folder for TMC project files -> home/IdeaProjects/TMCProjects .
     */
    public SettingsTmc() {
        logger.info("Setting default folder for TMC project files. @SettingsTmc");
        JFileChooser fileChooser = new JFileChooser();
        serverAddress = "https://tmc.mooc.fi/staging/org/tmc-intellij/";
        projectBasePath = fileChooser.getFileSystemView().getDefaultDirectory().toString()
                + File.separator + "IdeaProjects" + File.separator + "TMCProjects";
    }

    public void setUsername(String username) {
        logger.info("Setting username -> {}. @SettingsTmc", username);
        this.username = username;
    }

    public void setPassword(String password) {
        logger.info("Setting password. @SettingsTmc");
        this.password = password;
    }

    public void setServerAddress(String serverAddress) {
        logger.info("Setting server address -> {}. @SettingsTmc", serverAddress);
        this.serverAddress = serverAddress;
    }

    public String getProjectBasePath() {
        logger.info("Getting project base path <- {}. @SettingsTmc", projectBasePath);
        return projectBasePath;
    }

    public void setProjectBasePath(String projectBasePath) {
        logger.info("Setting project base path -> {}. @SettingsTmc", projectBasePath);
        if (projectBasePath.contains("TMCProjects")) {
            this.projectBasePath = projectBasePath;
        } else {
            this.projectBasePath = projectBasePath + File.separator + "TMCProjects";
        }
    }

    @Override
    public String getServerAddress() {
        logger.info("Getting server address <- {}. @SettingsTmc", serverAddress);
        return serverAddress;
    }

    @Override
    public String getPassword() {
        logger.info("Getting user password. @SettingsTmc");
        return password;
    }

    @Override
    public String getUsername() {
        logger.info("Getting username <- {}. @SettingsTmc", username);
        return username;
    }

    @Override
    public boolean userDataExists() {
        logger.info("Checking if user data exists. @SettingsTmc");
        return this.username != null && this.password != null
                && !this.username.isEmpty() && !this.password.isEmpty();
    }

    @Override
    public Optional<Course> getCurrentCourse() {
        return null;
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
        logger.info("Getting locale. @SettingsTmc");
        return new Locale("en");
    }

    @Override
    public SystemDefaultRoutePlanner proxy() {
        return null;
    }

    public Course getCourse() {
        logger.info("Getting course. @SettingsTmc");
        return course;
    }

    @Override
    public void setCourse(Course course) {
        logger.info("Setting course. @SettingsTmc");
        this.course = course;
    }

    @Override
    public void setConfigRoot(Path path) {

    }

    @Override
    public Path getConfigRoot() {
        return null;
    }
}
