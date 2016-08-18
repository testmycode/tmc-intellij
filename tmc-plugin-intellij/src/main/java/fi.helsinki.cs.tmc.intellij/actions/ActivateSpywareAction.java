package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.internal.statistic.UsagesCollector;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.event.DocumentListener;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.holders.TmcSettingsHolder;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.intellij.spyware.TextInputListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * This class substitutes the normal TypedActionHandler.
 * It attempts to add a listener to the current document when a key is pressed.
 * It also makes sure that the current course has all the necessary data for sending spyware events.
 */

public class ActivateSpywareAction implements TypedActionHandler {

    private TypedActionHandler handler;
    private static List<Document> listenedDocuments = new ArrayList<>();
    public ActivateSpywareAction(TypedActionHandler originalHandler) {
        handler = originalHandler;
    }
    private static final Logger logger = LoggerFactory.getLogger(ActivateSpywareAction.class);

    @Override
    public void execute(@NotNull final Editor editor, char c, @NotNull DataContext dataContext) {
        if (!listenedDocuments.contains(editor.getDocument()) && isThisCorrectProject()) {
            DocumentListener d = new TextInputListener();
            editor.getDocument().addDocumentListener(d);
            listenedDocuments.add(editor.getDocument());

            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TmcSettingsHolder.get().setCourse(PathResolver
                                .getCourse(ObjectFinder.findCurrentProject().getBasePath()));
                    } catch (Exception e) {
                    }
                }
            });

            logger.info("Added document listener to ", editor.getDocument().toString());
            UsagesCollector.doPersistProjectUsages(ObjectFinder.findCurrentProject());
        }
        handler.execute(editor, c, dataContext);
    }

    private Boolean isThisCorrectProject() {
        logger.info("Making sure current exercise should be tracked");
        return CourseAndExerciseManager.isCourseInDatabase(PathResolver
                .getCourseName(ObjectFinder.findCurrentProject().getBasePath()));
    }
}
