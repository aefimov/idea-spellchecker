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
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.intellij.spellChecker.engine.SpellChecker;
import org.intellij.spellChecker.engine.SpellCheckerFactory;
import org.intellij.spellChecker.options.SpellCheckerConfiguration;
import org.intellij.spellChecker.util.Strings;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Spell checker inspection provider.
 *
 * @author Sergiy Dubovik, Alexey Efimov
 */
public final class SpellCheckerManager {
    private static final int MAX_SUGGESTIONS_THRESHOLD = 10;

    public static SpellCheckerManager getInstance() {
        return ServiceManager.getService(SpellCheckerManager.class);
    }

    private final SpellCheckerConfiguration configuration;
    private final SpellChecker spellChecker = SpellCheckerFactory.create();

    public SpellCheckerManager(SpellCheckerConfiguration configuration) {
        this.configuration = configuration;
        reloadConfiguration();
    }

    @NotNull
    public static HighlightDisplayLevel getHighlightDisplayLevel() {
        return HighlightDisplayLevel.INFO;
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

    public List<String> getVariants(@NotNull String prefix) {
        return spellChecker.getVariants(prefix);
    }

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
        return Collections.emptyList();
    }

    /**
     * Load dictionary from stream.
     *
     * @param inputStream Dictionary input stream
     * @throws java.io.IOException if dictionary load with problems
     */
    public void addDictionary(@NotNull InputStream inputStream) throws IOException {
        addDictionary(inputStream, Charset.defaultCharset().name());
    }

    /**
     * Load dictionary from stream.
     *
     * @param inputStream Dictionary input stream
     * @param encoding    Encoding
     * @throws java.io.IOException if dictionary load with problems
     */
    public void addDictionary(@NotNull InputStream inputStream, @NonNls String encoding) throws IOException {
        addDictionary(inputStream, encoding, Locale.getDefault());
    }

    /**
     * Load dictionary from stream.
     *
     * @param inputStream Dictionary input stream
     * @param encoding    Encoding
     * @param locale      Locale of dictionary
     * @throws java.io.IOException if dictionary load with problems
     */
    public void addDictionary(@NotNull InputStream inputStream, @NonNls String encoding, @NonNls @NotNull Locale locale) throws IOException {
        spellChecker.addDictionary(inputStream, encoding, locale);
    }

    public void addToDictionary(@NotNull String word) {
        String lowerCased = word.toLowerCase();
        configuration.USER_DICTIONARY_WORDS.add(lowerCased);
        spellChecker.addToDictionary(lowerCased);
    }

    public void ignoreAll(@NotNull String word) {
        String lowerCased = word.toLowerCase();
        configuration.IGNORED_WORDS.add(lowerCased);
        spellChecker.ignoreAll(lowerCased);
    }

    public final Set<String> getIgnoredWords() {
        return configuration.IGNORED_WORDS;
    }

    public void reloadConfiguration() {
        spellChecker.reset();
        for (String word : ejectAll(configuration.IGNORED_WORDS)) {
            ignoreAll(word);
        }
        for (String word : ejectAll(configuration.USER_DICTIONARY_WORDS)) {
            addToDictionary(word);
        }
        restartInspections();
    }

    private static void restartInspections() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : projects) {
            if (project.isOpen() && !project.isDefault()) {
                DaemonCodeAnalyzer.getInstance(project).restart();
            }
        }
    }

    private HashSet<String> ejectAll(Set<String> from) {
        HashSet<String> words = new HashSet<String>(from);
        from.clear();
        return words;
    }
}
