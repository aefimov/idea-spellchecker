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

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiFile;
import org.intellij.spellChecker.util.SpellCheckerBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Inspection to string values.
 *
 * @author Alexey Efimov
 */
public class StringWithMistakesInspection extends LocalInspectionTool {
    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return SpellCheckerBundle.message("spelling");
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return SpellCheckerBundle.message("string.value.name.with.mistakes");
    }

    @NonNls
    @NotNull
    public String getShortName() {
        return "StringWithMistakes";
    }

    public boolean isEnabledByDefault() {
        return true;
    }

    @Nullable
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        StringsSpellCheckerVisitor visitor = new StringsSpellCheckerVisitor(manager);
        file.accept(visitor);
        return visitor.getProblems();
    }
}