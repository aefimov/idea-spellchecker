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
package org.intellij.spellChecker.inspections;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.lang.Language;
import com.intellij.psi.PsiFile;
import org.intellij.spellChecker.SpellCheckerManager;
import org.intellij.spellChecker.util.SpellCheckerBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Checks strings in property files.
 *
 * @author Sergiy Dubovik
 */
public class AdvancedPropertiesSpellingInspection extends LocalInspectionTool {
    @NonNls
    private static final String PROPERTIES = "Properties";

    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return SpellCheckerBundle.message("spelling");
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return SpellCheckerBundle.message("advanced.properties.spelling.inspection");
    }

    @NonNls
    @NotNull
    public String getShortName() {
        return "AdvancedPropertiesSpelling";
    }

    public boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return SpellCheckerManager.getHighlightDisplayLevel();
    }

    @Nullable
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        Language language = file.getLanguage();
        if (PROPERTIES.equals(language.getID())) {
            AbstractSpellCheckerVisitor visitor = new AdvancedPropertiesSpellingVisitor(manager);
            file.accept(visitor);
            return visitor.getProblems();
        }

        return null;
    }
}
