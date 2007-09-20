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
 * @author Sergiy Dubovik
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
            return spellChecker.getSuggestions(word, MAX_SUGGESTIONS_THRESHOLD);
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
