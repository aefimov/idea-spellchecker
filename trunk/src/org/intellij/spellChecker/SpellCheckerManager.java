package org.intellij.spellChecker;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellChecker;
import org.intellij.spellChecker.inspections.CommentsWithMistakesInspection;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Spell checker inspection provider.
 *
 * @author Sergiy Dubovik
 */
public final class SpellCheckerManager implements ApplicationComponent, InspectionToolProvider {
    private static final Logger LOG = Logger.getInstance("#SpellChecker");
    @NonNls
    private static final String DICT_ENGLISH = "/dict/english.0";

    public static SpellCheckerManager getInstance() {
        return ApplicationManager.getApplication().getComponent(SpellCheckerManager.class);
    }

    private static final Class[] INSPECTIONS = {
            CommentsWithMistakesInspection.class
    };

    private final SpellChecker spellChecker = new SpellChecker();

    public void initComponent() {
        InputStream is = SpellCheckerManager.class.getResourceAsStream(DICT_ENGLISH);
        if (is != null) {
            try {
                spellChecker.addDictionary(new SpellDictionaryHashMap(new InputStreamReader(is)));
            } catch (IOException e) {
                LOG.warn(e);
            }
        }
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "SpellChecker";
    }

    public Class[] getInspectionClasses() {
        return INSPECTIONS;
    }

    @NotNull
    public SpellChecker getSpellChecker() {
        return spellChecker;
    }
}
