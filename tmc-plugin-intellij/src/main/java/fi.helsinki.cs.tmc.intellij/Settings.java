import com.google.common.base.Optional;
import fi.helsinki.cs.tmc.core.configuration.TmcSettings;
import fi.helsinki.cs.tmc.core.domain.Course;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import java.nio.file.Path;
import java.util.Locale;

/**
 * Created by konstaku on 13.7.2016.
 */
public class Settings implements TmcSettings {

    @Override
    public String getServerAddress() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean userDataExists() {
        return false;
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
