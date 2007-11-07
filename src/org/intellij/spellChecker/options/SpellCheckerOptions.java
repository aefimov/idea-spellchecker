/*
 * Copyright 2007 Sergiy Dubovik, Alexey Efimov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.spellChecker.options;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.AddDeleteListPanel;
import com.intellij.util.containers.HashSet;
import org.intellij.spellChecker.SpellCheckerManager;
import org.intellij.spellChecker.util.SpellCheckerBundle;
import org.intellij.spellChecker.util.Strings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Spell checker options form.
 *
 * @author Alexey Efimov
 */
public class SpellCheckerOptions implements Disposable {
    private final SpellCheckerConfiguration configuration;

    private JPanel root;
    private WordsPanel userDictionaryWords;
    private WordsPanel ignoredWords;

    public SpellCheckerOptions(SpellCheckerConfiguration configuration) {
        this.configuration = configuration;
    }

    private void createUIComponents() {
        userDictionaryWords = new WordsPanel(configuration.USER_DICTIONARY_WORDS);
        ignoredWords = new WordsPanel(configuration.IGNORED_WORDS);
    }

    public Set<String> getUserDictionaryWords() {
        return getWords(userDictionaryWords);
    }

    public void setUserDictionaryWords(Set<String> words) {
        userDictionaryWords.replaceAll(words);
    }

    public Set<String> getIgnoredWords() {
        return getWords(ignoredWords);
    }

    public void setIgnoredWords(Set<String> words) {
        ignoredWords.replaceAll(words);
    }

    public JPanel getRoot() {
        return root;
    }

    private static Set<String> getWords(AddDeleteListPanel panel) {
        Set<String> words = new HashSet<String>();
        Object[] objects = panel.getListItems();
        for (Object object : objects) {
            words.add((String) object);
        }
        return words;
    }

    public void dispose() {
        userDictionaryWords.dispose();
        ignoredWords.dispose();
    }

    private static final class WordsPanel extends AddDeleteListPanel implements Disposable {
        private WordsPanel(Set<String> words) {
            super(null, sort(words));
        }

        private static List<String> sort(Set<String> words) {
            List<String> arrayList = new ArrayList<String>(words);
            Collections.sort(arrayList);
            return arrayList;
        }

        protected Object findItemToAdd() {
            String word = Messages.showInputDialog(SpellCheckerBundle.message("enter.simple.word"), SpellCheckerBundle.message("add.new.word"), null);
            if (word == null) {
                return null;
            } else {
                word = word.trim();
            }
            SpellCheckerManager checkerManager = SpellCheckerManager.getInstance();
            if (Strings.isMixedCase(word)) {
                Messages.showWarningDialog(SpellCheckerBundle.message("entered.word.0.is.mixed.cased.you.must.enter.simple.word", word), SpellCheckerBundle.message("add.new.word"));
                return null;
            }
            if (!checkerManager.hasProblem(word)) {
                Messages.showWarningDialog(SpellCheckerBundle.message("entered.word.0.is.correct.you.no.need.to.add.this.in.list", word), SpellCheckerBundle.message("add.new.word"));
                return null;
            }
            return word;
        }

        public void replaceAll(Set<String> words) {
            myList.clearSelection();
            myListModel.removeAllElements();
            for (String word : sort(words)) {
                myListModel.addElement(word);
            }
        }

        public void dispose() {
            myListModel.removeAllElements();
        }
    }
}
