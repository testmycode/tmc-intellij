package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.intellij.services.exercises.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.spyware.TextInputListener;

import com.intellij.internal.statistic.UsagesCollector;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.event.DocumentListener;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * This class substitutes the normal TypedActionHandler.
 * It attempts to add a listener to the current document when a key is pressed.
 */

public class ActivateSpywareAction implements TypedActionHandler {

    private TypedActionHandler handler;

    private static List<Document> listenedDocuments = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(ActivateSpywareAction.class);

    public ActivateSpywareAction(TypedActionHandler originalHandler) {
        handler = originalHandler;
    }

    @Override
    public void execute(@NotNull final Editor editor, char cha, @NotNull DataContext dataContext) {
        if (!listenedDocuments.contains(editor.getDocument()) && isThisCorrectProject()) {
            DocumentListener docl = new TextInputListener();
            editor.getDocument().addDocumentListener(docl);
            listenedDocuments.add(editor.getDocument());
            logger.info("Added document listener to ", editor.getDocument().toString());
            UsagesCollector.doPersistProjectUsages(new ObjectFinder().findCurrentProject());
        }
        handler.execute(editor, cha, dataContext);
    }

    private Boolean isThisCorrectProject() {
        logger.info("Making sure current exercise should be tracked");
        return new CourseAndExerciseManager().isCourseInDatabase(PathResolver
                .getCourseName(new ObjectFinder().findCurrentProject().getBasePath()));
    }
}
