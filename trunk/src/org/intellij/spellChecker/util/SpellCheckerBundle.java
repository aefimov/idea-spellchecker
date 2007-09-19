package org.intellij.spellChecker.util;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

/**
 * Spell checker i18n bundle.
 *
 * @author Alexey Efimov
 */
public final class SpellCheckerBundle {
    @NonNls
    private static final String BUNDLE_NAME = "org.intellij.spellChecker.util.SpellCheckerBundle";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private SpellCheckerBundle() {
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME)String key, Object... params) {
        return CommonBundle.message(BUNDLE, key, params);
    }
}
