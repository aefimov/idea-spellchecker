package org.intellij.spellChecker;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * @author Sergiy Dubovik
 */
public class SpellCheckerGermanDictionary implements ApplicationComponent {
    private static final Logger LOG = Logger.getInstance("#SpellCheckerGermanDictionary");
    @NonNls
    private static final String DICT_URL = "/dict/german.0";
    @NonNls
    private static final String UTF_8 = "utf-8";

    private final SpellCheckerManager manager;

    public SpellCheckerGermanDictionary(SpellCheckerManager manager) {
        this.manager = manager;
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "SpellCheckerGermanDictionary";
    }

    public void initComponent() {
        InputStream is = SpellCheckerGermanDictionary.class.getResourceAsStream(DICT_URL);
        if (is != null) {
            try {
                manager.addDictionary(is, UTF_8, Locale.GERMAN);
            } catch (IOException e) {
                LOG.warn(e);
            }
        }
    }

    public void disposeComponent() {
    }
}
