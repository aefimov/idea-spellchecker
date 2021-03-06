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
package org.intellij.spellChecker.engine;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Spell checker.
 *
 * @author Alexey Efimov
 */
public interface SpellChecker {
    void addDictionary(@NotNull InputStream is, @NonNls String encoding, @NotNull Locale locale) throws IOException;

    void addToDictionary(@NotNull String word);

    void ignoreAll(@NotNull String word);

    boolean isIgnored(@NotNull String word);

    boolean isCorrect(@NotNull String word);

    @NotNull
    List<String> getSuggestions(@NotNull String word, int threshold);

    @NotNull
    List<String> getVariants(@NotNull String prefix);

    /**
     * This method must clean up user dictionary words and ignored words.
     */
    void reset();
}
