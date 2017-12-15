package fi.helsinki.cs.tmc.intellij.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class ObjectFinderTest {

    private ObjectFinder finder;

    @Before
    public void setUp() throws Exception {
        finder = new ObjectFinder();
    }

    @Test
    public void findCourseByNameWorks() throws Exception {
        final List<Course> courses = new ArrayList<>();
        courses.add(new Course("Jamaikan Nippuside"));
        final Course toFind = new Course("Namibian Saha");

        TmcCore core = mock(TmcCore.class);
        courses.add(toFind);
        when(core.listCourses(ProgressObserver.NULL_OBSERVER)).thenReturn(
                () -> courses
        );
        when(core.getCourseDetails(ProgressObserver.NULL_OBSERVER, toFind)).thenReturn(
                () -> toFind
        );
        assertEquals(toFind, finder.findCourseForTesting("Namibian Saha", "name", core));
    }

    @Test
    public void findCourseByNameReturnsNullWhenNotFound() throws Exception {
        final List<Course> courses = new ArrayList<>();

        courses.add(new Course("Jamaikan Nippuside"));
        Course toFind = new Course("Namibian Vasara");
        courses.add(toFind);
        TmcCore core = mock(TmcCore.class);
        when(core.listCourses(ProgressObserver.NULL_OBSERVER)).thenReturn(
                () -> courses
        );
        assertEquals(null, finder.findCourseForTesting("Namibian Saha", "name", core));
    }

}
