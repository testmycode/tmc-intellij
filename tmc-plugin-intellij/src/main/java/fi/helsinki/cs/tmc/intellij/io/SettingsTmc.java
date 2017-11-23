package fi.helsinki.cs.tmc.intellij.io;

import fi.helsinki.cs.tmc.core.configuration.TmcSettings;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.OauthCredentials;

import com.google.common.base.Optional;

import com.intellij.util.xmlb.annotations.Property;

import com.intellij.openapi.application.ApplicationInfo;

import fi.helsinki.cs.tmc.core.domain.Organization;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import javax.swing.JFileChooser;

/** TMC Settings component from Core, has all the necessary settings. */
public class SettingsTmc implements TmcSettings, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(SettingsTmc.class);
    @Property private String username;
    @Property private String password;
    @Property private Course course;
    @Property private Organization organization;
    @Property private String token;
    @Property private OauthCredentials oauthCredentials;
    private String serverAddress;
    private String projectBasePath;
    private boolean checkForExercises;
    private boolean spyware;
    private boolean sendDiagnostics;
    private boolean firstRun;

    public SettingsTmc(String serverAddress, String username, String password) {
        // TODO: do we have an initial value for organization, token, etc.?
        this.spyware = true;
        this.sendDiagnostics = true;
        this.checkForExercises = false;
        this.serverAddress = serverAddress;
        this.username = username;
        this.password = password;
        this.organization = new Organization("Default", "lolled", "default", "lolled", false);
        this.oauthCredentials = new OauthCredentials("oauth_application_id", "oauth_secret");
        this.token = "kissa";
        this.firstRun = true;
    }

    /** Sets the default folder for TMC project files -> home/IdeaProjects/TMCProjects . */
    public SettingsTmc() {
        spyware = true;
        this.sendDiagnostics = true;
        this.checkForExercises = false;
        this.firstRun = true;
        logger.info("Setting default folder for TMC project files. @SettingsTmc");
        JFileChooser fileChooser = new JFileChooser();
        serverAddress = "https://tmc.mooc.fi/mooc";
        projectBasePath =
                fileChooser.getFileSystemView().getDefaultDirectory().toString()
                        + File.separator
                        + "IdeaProjects"
                        + File.separator
                        + "TMCProjects";
    }

    public boolean isCheckForExercises() {
        return checkForExercises;
    }

    public void setCheckForExercises(boolean checkForExercises) {
        this.checkForExercises = checkForExercises;
    }

    public void setUsername(String username) {
        logger.info("Setting username -> {}. @SettingsTmc", username);
        if (username.trim().equals("")) {
            this.username = null;
        }
        this.username = username;
    }

    public void setSpyware(boolean spyware) {
        this.spyware = spyware;
    }

    public void setPassword(String password) {
        logger.info("Setting password. @SettingsTmc");
        if (password.trim().equals("")) {
            this.password = null;
        }
        this.password = password;
    }

    public String getCourseName() {
        logger.info("Getting course name <- {}. @SettingsTmc", course.getName());
        if (course != null) {
            return course.getName();
        }
        return null;
    }

    public void setServerAddress(String serverAddress) {
        logger.info("Setting server address -> {}. @SettingsTmc", serverAddress);
        this.serverAddress = serverAddress;
    }

    public String getProjectBasePath() {
        logger.info("Getting project base path <- {}. @SettingsTmc", projectBasePath);
        return projectBasePath;
    }

    public boolean isSpyware() {
        return spyware;
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
    public Optional<String> getPassword() {
        logger.info("Getting user password. @SettingsTmc");
        return Optional.fromNullable(password);
    }

    @Override
    public void setPassword(Optional<String> password) {
        logger.info("Setting password. @SettingsTmc");
        if (password.isPresent() && password.get().trim().equals("")) {
            this.password = null;
        }
        this.password = password.orNull();
    }

    @Override
    public Optional<String> getUsername() {
        logger.info("Getting username <- {}. @SettingsTmc", username);
        return Optional.fromNullable(username);
    }

    @Override
    public boolean userDataExists() {
        logger.info("Checking if user data exists. @SettingsTmc");
        return this.username != null
                && this.password != null
                && !this.username.isEmpty()
                && !this.password.isEmpty();
    }

    @Override
    public Optional<Course> getCurrentCourse() {
        logger.info("Getting current course <- {}. @SettingsTmc", course);
        return Optional.fromNullable(course);
    }

    @Override
    public String clientName() {
        return "idea_plugin";
    }

    @Override
    public String clientVersion() {
        return "1.0.2";
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
        // implement?
        return null;
    }

    public Course getCourse() {
        logger.info("Getting course <- {}. @SettingsTmc", course);
        return course;
    }

    @Override
    public void setCourse(Course course) {
        logger.info("Setting course -> {}. @SettingsTmc", course);
        this.course = course;
    }

    @Override
    public Path getConfigRoot() {
        JFileChooser fileChooser = new JFileChooser();
        return Paths.get(fileChooser.getFileSystemView().getDefaultDirectory().toString());
    }

    @Override
    public boolean getSendDiagnostics() {
        return this.sendDiagnostics;
    }

    @Override
    public Optional<OauthCredentials> getOauthCredentials() {
        logger.info("Getting oauthCredentials. @SettingsTmc");
        return Optional.fromNullable(this.oauthCredentials);
    }

    public void setOauthCredentials(Optional<OauthCredentials> oauthCredentials) {
        logger.info("Setting oauthCredentials. @SettingsTmc");
        this.oauthCredentials = oauthCredentials.orNull();
    }

    @Override
    public void setToken(Optional<String> token) {
        logger.info("Setting token. @SettingsTmc");
        this.token = token.orNull();
    }

    @Override
    public Optional<String> getToken() {
        logger.info("Getting token. @SettingsTmc");
        return Optional.fromNullable(token);
    }

    @Override
    public Optional<Organization> getOrganization() {
        logger.info("Getting organization <- {}", organization.getName());
        return Optional.fromNullable(this.organization);
    }

    @Override
    public void setOrganization(Optional<Organization> org) {
        if (org.isPresent()) {
            logger.info("Setting organization -> {}", org.get().getName());
        }
        this.organization = org.orNull();
    }

    public void setSendDiagnostics(boolean value) {
        this.sendDiagnostics = value;
    }

    @Override
    public String hostProgramName() {
        return ApplicationInfo.getInstance().getVersionName();
    }

    @Override
    public String hostProgramVersion() {
        return ApplicationInfo.getInstance().getFullVersion();
    }

    public void setFirstRun(boolean value) {
        this.firstRun = value;
    }

    public boolean getFirstRun() {
        return this.firstRun;
    }
}
