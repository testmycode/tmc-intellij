package fi.helsinki.cs.tmc.intellij.io;

import com.google.common.base.Optional;
import fi.helsinki.cs.tmc.core.configuration.TmcSettings;
import fi.helsinki.cs.tmc.core.domain.Course;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import java.nio.file.Path;
import java.util.Locale;

/**
 * Created by tuomokar on 15.7.2016.
 */
public class Settings implements TmcSettings {

    private String username;
    private String password;
    private String url;
    private Course course;
    private String workDir;

    public Settings(String serverAddress, String username, String password) {
        this.url = serverAddress;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getServerAddress() {
        return url;
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

    @Override
    public void setCourse(Course course) {

    }

    @Override
    public void setConfigRoot(Path path) {

    }

    @Override
    public Path getConfigRoot() {
        return null;
    }
}
