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
package org.intellij.spellChecker;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.SpellChecker;
import org.intellij.spellChecker.inspections.CommentsWithMistakesInspection;
import org.intellij.spellChecker.util.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Spell checker inspection provider.
 *
 * @author Sergiy Dubovik, Alexey Efimov
 */
@State(
        name = "SpellChecker",
        storages = {
        @Storage(
                id = "spellchecker",
                file = "$APP_CONFIG$/spellchecker.xml"
        )}
)
public final class SpellCheckerManager implements ApplicationComponent, InspectionToolProvider, PersistentStateComponent<SpellCheckerManager.State> {
    private static final int MAX_SUGGESTIONS_THRESHOLD = 10;

    public static SpellCheckerManager getInstance() {
        return ApplicationManager.getApplication().getComponent(SpellCheckerManager.class);
    }

    private static final Class[] INSPECTIONS = {
            CommentsWithMistakesInspection.class
    };

    private final SpellChecker spellChecker = new SpellChecker();
    private final State state = new State();

    public void initComponent() {
        for (String word : state.IGNORED_WORDS) {
            spellChecker.ignoreAll(word);
        }
        for (String word : state.USER_DICTIONARY_WORDS) {
            spellChecker.addToDictionary(word);
        }
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "SpellChecker";
    }

    public State getState() {
        return state;
    }

    public void loadState(State state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }

    public Class[] getInspectionClasses() {
        return INSPECTIONS;
    }

    @NotNull
    public SpellChecker getSpellChecker() {
        return spellChecker;
    }

    public boolean hasProblem(String word) {
        return !spellChecker.isIgnored(word) && !spellChecker.isCorrect(word);
    }

    @SuppressWarnings({"unchecked"})
    public List<Word> getSuggestions(String word) {
        if (!spellChecker.isIgnored(word) && !spellChecker.isCorrect(word)) {
            List<Word> suggestions = spellChecker.getSuggestions(word, MAX_SUGGESTIONS_THRESHOLD);
            if (suggestions.size() != 0) {
                boolean capitalized = WordUtils.isCapitalized(word);
                boolean upperCases = WordUtils.isUpperCase(word);

                if (capitalized) {
                    WordUtils.capitalize(suggestions);
                } else if (upperCases) {
                    WordUtils.upperCase(suggestions);
                }
            }

            return suggestions;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Load dictionary from stream.
     *
     * @param inputStream Dictionary input stream
     * @throws java.io.IOException if dictionary load with problems
     */
    public void addDictionary(InputStream inputStream) throws IOException {
        spellChecker.addDictionary(new SpellDictionaryHashMap(new InputStreamReader(inputStream)));
    }

    public void addToDictionary(String word) {
        state.USER_DICTIONARY_WORDS.add(word);
        spellChecker.addToDictionary(word);
    }

    public void ignoreAll(String word) {
        state.IGNORED_WORDS.add(word);
        spellChecker.ignoreAll(word);
    }

    public final static class State {
        public Set<String> USER_DICTIONARY_WORDS = new HashSet<String>();
        public Set<String> IGNORED_WORDS = new HashSet<String>();
    }
}
