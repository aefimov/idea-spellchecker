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

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.intellij.spellChecker.SpellCheckerManager;
import org.intellij.spellChecker.util.SpellCheckerBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Set;

/**
 * Configurable for spelling options.
 *
 * @author Alexey Efimov
 */
public final class SpellCheckerConfigurable implements Configurable {

    private final SpellCheckerManager manager;
    private final SpellCheckerConfiguration configuration;

    private SpellCheckerOptions options;

    public SpellCheckerConfigurable(SpellCheckerManager manager, SpellCheckerConfiguration configuration) {
        this.manager = manager;
        this.configuration = configuration;
    }

    @Nls
    public String getDisplayName() {
        return SpellCheckerBundle.message("spelling");
    }

    @Nullable
    public Icon getIcon() {
        return null;
    }

    @Nullable
    @NonNls
    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (options == null) {
            options = new SpellCheckerOptions(configuration);
        }
        return options.getRoot();
    }

    public boolean isModified() {
        if (options != null) {
            if (!same(options.getUserDictionaryWords(), configuration.USER_DICTIONARY_WORDS)) {
                return true;
            }
            if (!same(options.getIgnoredWords(), configuration.IGNORED_WORDS)) {
                return true;
            }
        }
        return false;
    }

    private static boolean same(Set<String> modified, Set<String> original) {
        if (original.size() != modified.size()) {
            return false;
        }
        modified.removeAll(original);
        return modified.size() == 0;
    }

    public void apply() throws ConfigurationException {
        if (options != null) {
            replaceAll(configuration.USER_DICTIONARY_WORDS, options.getUserDictionaryWords());
            replaceAll(configuration.IGNORED_WORDS, options.getIgnoredWords());
            manager.reloadAndRestartInspections();
        }
    }

    private static void replaceAll(Set<String> words, Set<String> newWords) {
        words.clear();
        words.addAll(newWords);
    }

    public void reset() {
        if (options != null) {
            options.setUserDictionaryWords(configuration.USER_DICTIONARY_WORDS);
            options.setIgnoredWords(configuration.IGNORED_WORDS);
        }
    }

    public void disposeUIResources() {
        if (options != null) {
            options.dispose();
            options = null;
        }
    }
}
