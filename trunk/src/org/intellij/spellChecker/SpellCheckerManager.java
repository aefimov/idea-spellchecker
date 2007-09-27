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
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.SeverityRegistrar;
import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.intellij.spellChecker.engine.SpellChecker;
import org.intellij.spellChecker.engine.SpellCheckerFactory;
import org.intellij.spellChecker.inspections.*;
import org.intellij.spellChecker.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

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
    public static final HighlightSeverity SPELLING = new HighlightSeverity("SPELLING", HighlightSeverity.WARNING.myVal);

    private static final int MAX_SUGGESTIONS_THRESHOLD = 10;

    public static SpellCheckerManager getInstance() {
        return ApplicationManager.getApplication().getComponent(SpellCheckerManager.class);
    }

    private static final Class[] INSPECTIONS = {
            CommentsWithMistakesInspection.class,
            ClassNameWithMistakesInspection.class,
            MethodNameWithMistakesInspection.class,
            FieldNameWithMistakesInspection.class,
            AdvancedXmlSpellingInspection.class,
            AdvancedPropertiesSpellingInspection.class
    };

    private final SpellChecker spellChecker = SpellCheckerFactory.create();
    private final State state = new State();

    public void initComponent() {
        initSeverity();

        for (String word : state.IGNORED_WORDS) {
            spellChecker.ignoreAll(word);
        }
        for (String word : state.USER_DICTIONARY_WORDS) {
            spellChecker.addToDictionary(word);
        }
    }

    private void initSeverity() {
        SeverityRegistrar severityRegistrar = SeverityRegistrar.getInstance();

        if (!severityRegistrar.isSeverityValid(SPELLING)) {
            TextAttributes warningTextAttr = CodeInsightColors.WARNINGS_ATTRIBUTES.getDefaultAttributes();

            HighlightInfoType.HighlightInfoTypeImpl hiti =
                    new HighlightInfoType.HighlightInfoTypeImpl(SPELLING, CodeInsightColors.WARNINGS_ATTRIBUTES);


            if (warningTextAttr != null) {
                SeverityRegistrar.SeverityBasedTextAttributes attributes =
                        new SeverityRegistrar.SeverityBasedTextAttributes(warningTextAttr, hiti);

                List<String> severites = new ArrayList<String>();
                for (int i = 0; i < severityRegistrar.getSeveritiesCount(); i++) {
                    severites.add(severityRegistrar.getSeverityByIndex(i).myName);
                }

                severityRegistrar.registerSeverity(attributes, Color.YELLOW);

                severites.add(SPELLING.myName);
                severityRegistrar.setOrder(severites);
            }
        }

        HighlightDisplayLevel.registerSeverity(SPELLING, CodeInsightColors.WARNINGS_ATTRIBUTES.getDefaultAttributes().getErrorStripeColor());
    }

    public static HighlightDisplayLevel getHighlightDisplayLevel() {
        return HighlightDisplayLevel.find(SPELLING);
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
    public List<String> getSuggestions(String word) {
        if (!spellChecker.isIgnored(word) && !spellChecker.isCorrect(word)) {
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
    public void addDictionary(InputStream inputStream) throws IOException {
        spellChecker.addDictionary(inputStream);
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
