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

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.intellij.spellChecker.engine.SpellChecker;
import org.intellij.spellChecker.engine.SpellCheckerFactory;
import org.intellij.spellChecker.inspections.*;
import org.intellij.spellChecker.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
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
            CommentsWithMistakesInspection.class,
            ClassNameWithMistakesInspection.class,
            MethodNameWithMistakesInspection.class,
            FieldNameWithMistakesInspection.class,
            LocalVariableNameWithMistakesInspection.class,
            StringWithMistakesInspection.class,
            AdvancedXmlSpellingInspection.class,
            AdvancedPropertiesSpellingInspection.class,
    };

    private final SpellChecker spellChecker = SpellCheckerFactory.create();
    private final State state = new State();

    public void initComponent() {
        for (String word : ejectAll(state.IGNORED_WORDS)) {
            ignoreAll(word);
        }
        for (String word : ejectAll(state.USER_DICTIONARY_WORDS)) {
            addToDictionary(word);
        }
    }

    private HashSet<String> ejectAll(Set<String> from) {
        HashSet<String> words = new HashSet<String>(from);
        from.clear();
        return words;
    }

    @NotNull
    public static HighlightDisplayLevel getHighlightDisplayLevel() {
        return HighlightDisplayLevel.INFO;
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

    public boolean hasProblem(@NotNull String word) {
        return !isIgnored(word) && !spellChecker.isCorrect(word);
    }

    private boolean isIgnored(@NotNull String word) {
        return spellChecker.isIgnored(word.toLowerCase());
    }

    @SuppressWarnings({"unchecked"})
    @NotNull
    public List<String> getSuggestions(@NotNull String word) {
        if (!isIgnored(word) && !spellChecker.isCorrect(word)) {
            List<String> suggestions = spellChecker.getSuggestions(word, MAX_SUGGESTIONS_THRESHOLD);
            if (suggestions.size() != 0) {
                boolean capitalized = Strings.isCapitalized(word);
                boolean upperCases = Strings.isUpperCase(word);

                if (capitalized) {
                    Strings.capitalize(suggestions);
                } else if (upperCases) {
                    Strings.upperCase(suggestions);
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
    public void addDictionary(@NotNull InputStream inputStream) throws IOException {
        spellChecker.addDictionary(inputStream);
    }

    public void addToDictionary(@NotNull String word) {
        String lowerCased = word.toLowerCase();
        state.USER_DICTIONARY_WORDS.add(lowerCased);
        spellChecker.addToDictionary(lowerCased);
    }

    public void ignoreAll(@NotNull String word) {
        String lowerCased = word.toLowerCase();
        state.IGNORED_WORDS.add(lowerCased);
        spellChecker.ignoreAll(lowerCased);
    }

    public final static class State {
        public Set<String> USER_DICTIONARY_WORDS = new HashSet<String>();
        public Set<String> IGNORED_WORDS = new HashSet<String>();
    }
}
