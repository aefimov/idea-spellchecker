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

import com.intellij.util.containers.HashSet;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * Jazzy implementation of Spell Checker.
 *
 * @author Alexey Efimov
 */
final class JazzySpellChecker implements SpellChecker {
    private final com.swabunga.spell.event.SpellChecker delegate = new com.swabunga.spell.event.SpellChecker();
    private final Map<SpellDictionaryImpl, Set<Character>> dictionaries = new HashMap<SpellDictionaryImpl, Set<Character>>();
    private final Set<Character> allowed = new HashSet<Character>();
    private SpellDictionaryImpl userDictionary;

    JazzySpellChecker() {
        setUserDictionary();
    }

    public void addDictionary(@NotNull InputStream is, @NonNls String encoding, @NotNull Locale locale) throws IOException {
        SpellDictionaryImpl spellDictionary = new SpellDictionaryImpl(new InputStreamReader(is, encoding), locale);
        Set<Character> indexedChars = spellDictionary.getIndexedChars();
        dictionaries.put(spellDictionary, indexedChars);
        allowed.addAll(indexedChars);
        delegate.addDictionary(spellDictionary);
    }

    public void addToDictionary(@NotNull String word) {
        delegate.addToDictionary(word);
        indexWord(word);
    }

    private void indexWord(String word) {
        char[] chars = word.toCharArray();
        for (char c : chars) {
            allowed.add(c);
        }
    }

    public void ignoreAll(@NotNull String word) {
        delegate.ignoreAll(word);
    }

    public boolean isIgnored(@NotNull String word) {
        return !isEntireWordAllowed(word, allowed) || delegate.isIgnored(word);
    }

    public boolean isCorrect(@NotNull String word) {
        return !isEntireWordAllowed(word, allowed) || delegate.isCorrect(word);
    }

    private boolean isEntireWordAllowed(String word, Set<Character> index) {
        char[] chars = word.toCharArray();
        for (char c : chars) {
            if (!index.contains(c)) {
                return false;
            }
        }
        return true;
    }

    private Set<Character> findDictionaryIndex(String word) {
        Collection<Set<Character>> indexes = dictionaries.values();
        for (Set<Character> index : indexes) {
            if (isEntireWordAllowed(word, index)) {
                return index;
            }
        }
        return null;
    }

    @NotNull
    @SuppressWarnings({"unchecked"})
    public List<String> getSuggestions(@NotNull String word, int threshold) {
        List<Word> words = delegate.getSuggestions(word, threshold);
        Set<Character> index = findDictionaryIndex(word);
        List<String> strings = new ArrayList<String>(words.size());
        for (Word w : words) {
            String suggestion = w.getWord();
            if (index == null || isEntireWordAllowed(suggestion, index)) {
                strings.add(suggestion);
            }
        }
        return strings;
    }

    @NotNull
    public List<String> getVariants(@NotNull String prefix) {
        if (prefix.length() > 0) {
            List<String> variants = new ArrayList<String>();
            userDictionary.appendWordsStartsWith(prefix, variants);
            Set<Character> index = findDictionaryIndex(prefix);
            if (index != null) {
                for (SpellDictionaryImpl dictionary : dictionaries.keySet()) {
                    if (index.equals(dictionaries.get(dictionary))) {
                        dictionary.appendWordsStartsWith(prefix, variants);
                    }
                }
            }
            Collections.sort(variants);
            return variants;
        }
        return Collections.emptyList();
    }

    public void reset() {
        delegate.reset();
        allowed.clear();
        Collection<Set<Character>> sets = dictionaries.values();
        for (Set<Character> set : sets) {
            allowed.addAll(set);
        }
        setUserDictionary();
    }

    private void setUserDictionary() {
        try {
            userDictionary = new SpellDictionaryImpl(Locale.getDefault());
            delegate.setUserDictionary(userDictionary);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class SpellDictionaryImpl extends SpellDictionaryHashMap {
        private final Locale locale;

        public SpellDictionaryImpl(Locale locale) throws IOException {
            this.locale = locale;
        }

        private SpellDictionaryImpl(Reader wordList, Locale locale) throws IOException {
            super(wordList);
            this.locale = locale;
        }

        @SuppressWarnings({"unchecked"})
        public Set<Character> getIndexedChars() {
            Set<Character> index = new HashSet<Character>(64);
            Collection<List<String>> values = mainDictionary.values();
            for (List<String> wordList : values) {
                if (wordList != null) {
                    for (String word : wordList) {
                        if (word != null) {
                            char[] chars = word.toCharArray();
                            for (char c : chars) {
                                index.add(c);
                            }
                        }
                    }
                }
            }
            return index;
        }

        @SuppressWarnings({"unchecked"})
        public void appendWordsStartsWith(@NotNull String prefix, @NotNull Collection<String> buffer) {
            String prefixLowerCase = prefix.toLowerCase(locale);
            Collection<List<String>> values = mainDictionary.values();
            int prefixLength = prefix.length();
            StringBuilder builder = new StringBuilder();
            for (List<String> wordList : values) {
                if (wordList != null) {
                    for (String word : wordList) {
                        if (word != null) {
                            String lowerCased = word.toLowerCase(locale);
                            int length = lowerCased.length();
                            if (lowerCased.startsWith(prefixLowerCase) && length > prefixLength) {
                                builder.setLength(0);
                                builder.append(prefix);
                                builder.append(lowerCased, prefixLength, length);
                                String value = builder.toString();
                                if (!buffer.contains(value)) {
                                    buffer.add(value);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
