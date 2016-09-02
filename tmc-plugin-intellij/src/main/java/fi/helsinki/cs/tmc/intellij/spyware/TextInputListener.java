package fi.helsinki.cs.tmc.intellij.spyware;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.utilities.JsonMaker;
import fi.helsinki.cs.tmc.intellij.services.ClipboardService;
import fi.helsinki.cs.tmc.intellij.services.Exercises.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.spyware.LoggableEvent;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;

import name.fraser.neil.plaintext.DiffMatchPatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * When a change in the listened document happens this class creates a diff patch.
 * That created patch is then analyzed and a json is generated from it that is added
 * to the list of items to be sent to the spyware server.
 */
public class TextInputListener implements DocumentListener {

    private static final Logger logger = LoggerFactory.getLogger(TextInputListener.class);

    private DiffMatchPatch diff = new DiffMatchPatch();
    private String previous;
    private String modified;

    @Override
    public void beforeDocumentChange(DocumentEvent documentEvent) {
        previous = documentEvent.getDocument().getText();
    }

    @Override
    public void documentChanged(DocumentEvent documentEvent) {
        modified = documentEvent.getDocument().getText();
        if (!isThisCorrectProject() || changeIsNotJustWhitespace(documentEvent)) {
            logger.info("not creating path for event, as project wasn't "
                    + "correct one or change was just white space");
            return;
        }

        logger.info("Creating patches for ", documentEvent.getSource());
        createPatches(PathResolver.getExercise(new ObjectFinder()
                        .findCurrentProject().getBasePath()), documentEvent);
    }

    private boolean isThisCorrectProject() {
        return new CourseAndExerciseManager().isCourseInDatabase(PathResolver
                .getCourseName(new ObjectFinder().findCurrentProject().getBasePath()));
    }

    private boolean changeIsNotJustWhitespace(DocumentEvent documentEvent) {
        return !documentEvent
                .getNewFragment().toString().trim().isEmpty() || !documentEvent
                .getOldFragment().toString().trim().isEmpty();
    }

    private void createPatches(Exercise exercise, DocumentEvent documentEvent) {
        List<DiffMatchPatch.Patch> patches;
        patches = diff.patch_make(previous, modified);

        if (isRemoveEvent(documentEvent)) {
            addEventToManager(exercise, "text_remove",
                    generatePatchDescription(documentEvent, patches));
        } else if (isPasteEvent(documentEvent)) {
            addEventToManager(exercise, "text_paste",
                    generatePatchDescription(documentEvent, patches));
        } else {
            addEventToManager(exercise, "text_insert",
                    generatePatchDescription(documentEvent, patches));
        }
    }

    private boolean isPasteEvent(DocumentEvent documentEvent) {
        if (ClipboardService.getClipBoard() == null) {
            return false;
        }
        return (documentEvent.getNewLength() > 2)
                && ClipboardService.getClipBoard().trim().equals(
                        documentEvent.getNewFragment()
                        .toString().trim());
    }

    private String generatePatchDescription(DocumentEvent documentEvent,
                                            List<DiffMatchPatch.Patch> patches) {

        logger.info("Creating JSON from patches.");
        String source = documentEvent.getSource().toString();

        if (documentEvent.getSource().toString().length() <= 20) {
            return null;
        }

        source = source.substring(20, source.length() - 1);
        return JsonMaker.create()
                .add("file", new PathResolver().getPathRelativeToProject(source))
                .add("patches", diff.patch_toText(patches))
                .add("full_document",
                        documentEvent
                                .getNewLength() == documentEvent.getDocument().getTextLength())
                .toString();
    }

    private boolean isRemoveEvent(DocumentEvent documentEvent) {
        return (documentEvent.getOldLength() > 0 && documentEvent.getNewLength() == 0);
    }

    private void addEventToManager(Exercise exercise, String eventType, String text) {
        if (text == null) {
            return;
        }

        LoggableEvent event = new LoggableEvent(exercise, eventType, text.getBytes());
        SpywareEventManager.add(event);


    }


}
