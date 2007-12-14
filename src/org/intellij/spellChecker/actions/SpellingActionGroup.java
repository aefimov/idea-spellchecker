package org.intellij.spellChecker.actions;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.ShowIntentionsPass;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ex.QuickFixWrapper;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiCodeFragment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.intellij.spellChecker.util.SpellCheckerBundle;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Spelling action group.
 *
 * @author Alexey Efimov
 */
public class SpellingActionGroup extends ActionGroup {
    public SpellingActionGroup() {
    }

    public SpellingActionGroup(String shortName, boolean popup) {
        super(shortName, popup);
    }

    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        if (e != null) {
            PsiFile psiFile = e.getData(DataKeys.PSI_FILE);
            Project project = e.getData(DataKeys.PROJECT);
            Editor editor = e.getData(DataKeys.EDITOR);
            if (psiFile != null && project != null && editor != null) {
                PsiDocumentManager.getInstance(project).commitAllDocuments();
                if (HintManager.getInstance().performCurrentQuestionAction()) {
                    return AnAction.EMPTY_ARRAY;
                }
                if (!psiFile.isWritable()) {
                    return AnAction.EMPTY_ARRAY;
                }
                if (psiFile instanceof PsiCodeFragment) {
                    return AnAction.EMPTY_ARRAY;
                }
                TemplateState templatestate = TemplateManagerImpl.getTemplateState(editor);
                if (templatestate != null && !templatestate.isFinished()) {
                    return AnAction.EMPTY_ARRAY;
                }
                DaemonCodeAnalyzer.getInstance(project).autoImportReferenceAtCursor(editor, psiFile);
                com.intellij.codeInsight.intention.IntentionAction aintentionaction[] = IntentionManager.getInstance().getIntentionActions();
                ArrayList<HighlightInfo.IntentionActionDescriptor> arraylist1 = new ArrayList<HighlightInfo.IntentionActionDescriptor>();
                ArrayList<HighlightInfo.IntentionActionDescriptor> arraylist2 = new ArrayList<HighlightInfo.IntentionActionDescriptor>();
                ArrayList<HighlightInfo.IntentionActionDescriptor> arraylist3 = new ArrayList<HighlightInfo.IntentionActionDescriptor>();

                ShowIntentionsPass.getActionsToShow(editor, psiFile, arraylist1, arraylist2, arraylist3, aintentionaction, -1);
                String family = SpellCheckerBundle.message("spelling");
                printLocalQuickFix(arraylist1, family);
                printLocalQuickFix(arraylist2, family);
                printLocalQuickFix(arraylist3, family);
            }
        }
        return AnAction.EMPTY_ARRAY;
    }

    private static void printLocalQuickFix(ArrayList<HighlightInfo.IntentionActionDescriptor> actions, String family) {
        for (HighlightInfo.IntentionActionDescriptor actionDescriptor : actions) {
            IntentionAction action = actionDescriptor.getAction();
            System.out.println("action = " + action.getClass());
            if (action instanceof QuickFixWrapper) {
                QuickFixWrapper wrapper = (QuickFixWrapper) action;
                LocalQuickFix localQuickFix = wrapper.getFix();
                if (localQuickFix.getClass().getName().startsWith("org.intellij.spellChecker.inspections")) {
                    System.out.println("localQuickFix = " + localQuickFix.getClass());
                }
            }
        }
    }

    public void update(AnActionEvent e) {
        super.update(e);
    }
}
