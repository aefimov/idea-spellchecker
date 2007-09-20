package org.intellij.spellChecker;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * English dictionary.
 *
 * @author Alexey Efimov
 */
public class SpellCheckerEnglishDictionary implements ApplicationComponent {
    private static final Logger LOG = Logger.getInstance("#SpellCheckerEnglishDictionary");
    @NonNls
    private static final String DICT_URL = "/dict/english.0";

    private final SpellCheckerManager manager;

    public SpellCheckerEnglishDictionary(SpellCheckerManager manager) {
        this.manager = manager;
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "SpellCheckerEnglishDictionary";
    }

    public void initComponent() {
        InputStream is = SpellCheckerEnglishDictionary.class.getResourceAsStream(DICT_URL);
        if (is != null) {
            try {
                manager.addDictionary(is);
            } catch (IOException e) {
                LOG.warn(e);
            }
        }
    }

    public void disposeComponent() {
    }
}
