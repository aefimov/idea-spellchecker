package org.intellij.spellChecker.actions;

import com.intellij.codeInsight.completion.DefaultCharFilter;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.lookup.LookupItemPreferencePolicy;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.intellij.spellChecker.SpellCheckerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Completion for spell checker.
 *
 * @author Sergiy Dubovik
 */
public final class SpellCheckerCompletion extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(DataKeys.PROJECT);
        Editor editor = e.getData(DataKeys.EDITOR);
        PsiFile psiFile = e.getData(DataKeys.PSI_FILE);
        if (project != null && editor != null && psiFile != null) {
            int documentOffset = editor.getCaretModel().getOffset() - 1;

            if (documentOffset > 0) {
                StringBuffer buffer = new StringBuffer();
                char ch = editor.getDocument().getCharsSequence().charAt(documentOffset);
                while (Character.isJavaIdentifierPart(ch)) {
                    if (documentOffset == 0) {
                        break;
                    }

                    buffer.append(ch);
                    documentOffset -= 1;
                    ch = editor.getDocument().getCharsSequence().charAt(documentOffset);
                }

                String word = buffer.reverse().toString();
                List<String> suggestions = SpellCheckerManager.getInstance().getSuggestions(word);
                List<LookupItem<String>> lookupItems = new ArrayList<LookupItem<String>>();
                for (String suggestion : suggestions) {
                    lookupItems.add(new LookupItem<String>(suggestion, suggestion));
                }

                LookupItem[] items = new LookupItem[lookupItems.size()];
                items = lookupItems.toArray(items);
                LookupManager lookupManager = LookupManager.getInstance(project);
                lookupManager.showLookup(editor, items, word, new JazzyLookupItemPreferencePolicy(), new DefaultCharFilter(editor, psiFile, 0));
            }
        }
    }

    public void update(AnActionEvent e) {
        super.update(e);
        Project project = e.getData(DataKeys.PROJECT);
        Editor editor = e.getData(DataKeys.EDITOR);
        PsiFile psiFile = e.getData(DataKeys.PSI_FILE);
        boolean available = project != null && editor != null && psiFile != null;
        Presentation presentation = e.getPresentation();
        if (presentation.isVisible()) {
            presentation.setVisible(available);
        }
        if (presentation.isEnabled()) {
            presentation.setEnabled(available);
        }
    }

    private static final class JazzyLookupItemPreferencePolicy implements LookupItemPreferencePolicy {
        public void setPrefix(String prefix) {
        }

        public void itemSelected(LookupItem lookupItem) {
        }

        public int compare(LookupItem o1, LookupItem o2) {
            return o1.getLookupString().compareTo(o2.getLookupString());
        }
    }
}
