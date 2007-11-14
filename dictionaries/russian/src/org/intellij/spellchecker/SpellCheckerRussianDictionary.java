package org.intellij.spellchecker;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import org.intellij.spellChecker.SpellCheckerManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * @author Sergiy Dubovik
 */
public class SpellCheckerRussianDictionary implements ApplicationComponent {
    private static final Logger LOG = Logger.getInstance("#SpellCheckerRussianDictionary");
    @NonNls
    private static final Locale LOCALE = new Locale("ru");
    @NonNls
    private static final String DICT_URL = "/dict/russian.0";
    @NonNls
    private static final String UTF_8 = "utf-8";

    private final SpellCheckerManager manager;

    public SpellCheckerRussianDictionary(SpellCheckerManager manager) {
        this.manager = manager;
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "SpellCheckerRussianDictionary";
    }

    public void initComponent() {
        InputStream is = SpellCheckerRussianDictionary.class.getResourceAsStream(DICT_URL);
        if (is != null) {
            try {
                manager.addDictionary(is, UTF_8, LOCALE);
            } catch (IOException e) {
                LOG.warn(e);
            }
        }
    }

    public void disposeComponent() {
    }
}
