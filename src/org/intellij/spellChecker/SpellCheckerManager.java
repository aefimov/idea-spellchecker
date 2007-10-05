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
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
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
public final class SpellCheckerManager implements ApplicationComponent, InspectionToolProvider, PersistentStateComponent<SpellCheckerManager.State>, ProjectManagerListener {
    private static final HighlightSeverity SPELLING = new HighlightSeverity("SPELLING", HighlightSeverity.INFO.myVal);
    private static final TextAttributesKey SPELLING_ATTRIBUTES = TextAttributesKey.createTextAttributesKey("SPELLING_ATTRIBUTES");
    private static final int MAX_SUGGESTIONS_THRESHOLD = 10;

    private final ProjectManager projectManager;

    public static SpellCheckerManager getInstance() {
        return ApplicationManager.getApplication().getComponent(SpellCheckerManager.class);
    }

    public SpellCheckerManager(ProjectManager projectManager) {
        this.projectManager = projectManager;
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
        projectManager.addProjectManagerListener(this);

        initSeverity(SeverityRegistrar.getInstance());
        TextAttributes sta = getSpellingTextAttributes();
        HighlightDisplayLevel.registerSeverity(SPELLING, sta.getErrorStripeColor());

        for (String word : ejectAll(state.IGNORED_WORDS)) {
            ignoreAll(word);
        }
        for (String word : ejectAll(state.USER_DICTIONARY_WORDS)) {
            addToDictionary(word);
        }
    }

    public void projectOpened(Project project) {
        initSeverity(SeverityRegistrar.getInstance(project));
    }

    public boolean canCloseProject(Project project) {
        return true;
    }

    public void projectClosed(Project project) {
        disposeSeverity(SeverityRegistrar.getInstance(project));
    }

    public void projectClosing(Project project) {
    }

    private HashSet<String> ejectAll(Set<String> from) {
        HashSet<String> words = new HashSet<String>(from);
        from.clear();
        return words;
    }

    private void initSeverity(SeverityRegistrar severityRegistrar) {
        TextAttributes sta = getSpellingTextAttributes();
        if (!severityRegistrar.isSeverityValid(SPELLING)) {
            HighlightInfoType.HighlightInfoTypeImpl hiti = new HighlightInfoType.HighlightInfoTypeImpl(SPELLING, SPELLING_ATTRIBUTES);
            SeverityRegistrar.SeverityBasedTextAttributes attributes = new SeverityRegistrar.SeverityBasedTextAttributes(sta, hiti);

            List<String> severites = new ArrayList<String>();
            for (int i = 0; i < severityRegistrar.getSeveritiesCount(); i++) {
                severites.add(severityRegistrar.getSeverityByIndex(i).myName);
            }

            severityRegistrar.registerSeverity(attributes, sta.getErrorStripeColor());

            severites.add(SPELLING.myName);
            severityRegistrar.setOrder(severites);
        }
    }

    private void disposeSeverity(SeverityRegistrar severityRegistrar) {
        severityRegistrar.unregisterSeverity(SPELLING);
    }

    @NotNull
    private static TextAttributes getSpellingTextAttributes() {
        TextAttributes infoAttrs = CodeInsightColors.INFO_ATTRIBUTES.getDefaultAttributes();
        if (infoAttrs == null) {
            infoAttrs = new TextAttributes(null, null, new Color(204, 204, 204), EffectType.WAVE_UNDERSCORE, 0);
            infoAttrs.setErrorStripeColor(new Color(255, 255, 204));
        }
        return infoAttrs;
    }

    @NotNull
    public static HighlightDisplayLevel getHighlightDisplayLevel() {
        HighlightDisplayLevel level = HighlightDisplayLevel.find(SPELLING);
        return level != null ? level : HighlightDisplayLevel.WARNING;
    }

    public void disposeComponent() {
        disposeSeverity(SeverityRegistrar.getInstance());
        projectManager.removeProjectManagerListener(this);
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
