package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.internal.statistic.UsagesCollector;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diff.impl.incrementalMerge.ui.EditorPlace;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.ui.tabs.TabsUtil;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.holders.TmcSettingsHolder;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.intellij.spyware.TextInputListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ActivateSpywareAction implements TypedActionHandler {

    private TypedActionHandler handler;
    private static List<Document> listenedDocuments = new ArrayList<>();

    public ActivateSpywareAction(TypedActionHandler originalHandler) {
        handler = originalHandler;
    }

    @Override
    public void execute(@NotNull final Editor editor, char c, @NotNull DataContext dataContext) {
        if (!listenedDocuments.contains(editor.getDocument())) {
            DocumentListener d = new TextInputListener();
            editor.getDocument().addDocumentListener(d);
            listenedDocuments.add(editor.getDocument());
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TmcSettingsHolder.get().setCourse(PathResolver.getCourse(ObjectFinder.findCurrentProject().getBasePath()));
                    } catch (Exception e) {
                    }
                }
            });
            UsagesCollector.doPersistProjectUsages(ObjectFinder.findCurrentProject());
        }
        handler.execute(editor, c, dataContext);
    }
}
