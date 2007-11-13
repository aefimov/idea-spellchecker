package org.intellij.spellChecker;

import com.intellij.codeInsight.completion.DefaultCharFilter;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.lookup.LookupItemPreferencePolicy;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Sergiy Dubovik
 */
public class JazzyCompletion extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(DataKeys.PROJECT);
        Editor editor = e.getData(DataKeys.EDITOR);
        assert editor != null;

        int documentOffset = editor.getCaretModel().getOffset() - 1;

        if (documentOffset > 0) {
            StringBuffer buf = new StringBuffer();
            char ch = editor.getDocument().getCharsSequence().charAt(documentOffset);
            while(Character.isJavaIdentifierPart(ch)){
                if(documentOffset == 0)
                    break;

                buf.append(ch);
                documentOffset -= 1;
                ch = editor.getDocument().getCharsSequence().charAt(documentOffset);
            }

            String word = buf.reverse().toString();
            List<String> suggestions = SpellCheckerManager.getInstance().getSuggestions(word);
            List<LookupItem<String>> lookupItems = new ArrayList<LookupItem<String>>();
            for(String suggestion : suggestions) {
                lookupItems.add(new LookupItem<String>(suggestion, suggestion));
            }

            LookupItem<String>[] items = new LookupItem[lookupItems.size()];
            items = lookupItems.toArray(items);
            LookupManager lookupManager = LookupManager.getInstance(e.getData(DataKeys.PROJECT));
            lookupManager.showLookup(editor, items, word, new JazzyLookupItemPreferecePolicy(), new DefaultCharFilter(editor, e.getData(DataKeys.PSI_FILE), 0));
        }
    }

    class JazzyLookupItemPreferecePolicy implements LookupItemPreferencePolicy {
        public void setPrefix(String s) {
        }

        public void itemSelected(LookupItem lookupItem) {
        }

        public int compare(LookupItem o1, LookupItem o2) {
            return o1.getLookupString().compareTo(o2.getLookupString());
        }
    }
}
