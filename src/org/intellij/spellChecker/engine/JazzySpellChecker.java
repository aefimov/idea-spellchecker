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

import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Jazzy implementation of Spell Checker.
 *
 * @author Alexey Efimov
 */
final class JazzySpellChecker implements SpellChecker {
    public void addDictionary(InputStream is, String encoding) throws IOException {
        delegate.addDictionary(new SpellDictionaryHashMap(new InputStreamReader(is, encoding)));
    }

    public void addToDictionary(String word) {
        delegate.addToDictionary(word);
    }

    public void ignoreAll(String word) {
        delegate.ignoreAll(word);
    }

    public boolean isIgnored(String word) {
        return delegate.isIgnored(word);
    }

    public boolean isCorrect(String word) {
        return delegate.isCorrect(word);
    }

    @SuppressWarnings({"unchecked"})
    public List<String> getSuggestions(String word, int threshold) {
        List<Word> words = delegate.getSuggestions(word, threshold);
        List<String> strings = new ArrayList<String>(words.size());
        for (Word w : words) {
            strings.add(w.getWord());
        }
        return strings;
    }

    public void reset() {
        delegate.reset();
        try {
            delegate.setUserDictionary(new SpellDictionaryHashMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final com.swabunga.spell.event.SpellChecker delegate = new com.swabunga.spell.event.SpellChecker();
}
