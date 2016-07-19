package fi.helsinki.cs.tmc.intellij.io;

import com.google.common.base.Optional;
import fi.helsinki.cs.tmc.core.configuration.TmcSettings;
import fi.helsinki.cs.tmc.core.domain.Course;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Locale;

public class Settings implements TmcSettings, Serializable {

    private String username;
    private String password;
    private String serverAddress;
    private Course course;
    private String workDir;


    public Settings(String serverAddress, String username, String password) {
        this.serverAddress = serverAddress;
        this.username = username;
        this.password = password;
    }

    public Settings() {
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

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
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
        return null;
    }

    @Override
    public String apiVersion() {
        return null;
    }

    @Override
    public String clientName() {
        return null;
    }

    @Override
    public String clientVersion() {
        return null;
    }

    @Override
    public String getFormattedUserData() {
        return null;
    }

    @Override
    public Path getTmcProjectDirectory() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
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
        return null;
    }
}
