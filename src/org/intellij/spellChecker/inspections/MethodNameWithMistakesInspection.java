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
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.NameUtil;
import org.intellij.spellChecker.util.SpellCheckerBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Inspection to check method names.
 *
 * @author Alexey Efimov
 */
public class MethodNameWithMistakesInspection extends LocalInspectionTool {
    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return SpellCheckerBundle.message("spelling");
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return SpellCheckerBundle.message("method.name.with.mistakes");
    }

    @NonNls
    @NotNull
    public String getShortName() {
        return "MethodNameWithMistakes";
    }

    @Nullable
    public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        List<ProblemDescriptor> problems = null;
        String methodName = method.getName();
        PsiIdentifier psiName = method.getNameIdentifier();
        if (psiName != null) {
            String[] words = NameUtil.nameToWords(methodName);
            int offsetInParent = psiName.getStartOffsetInParent();
            int index = 0;
            for (String word : words) {
                int start = methodName.indexOf(word, index);
                int end = start + word.length();
                List<ProblemDescriptor> list = SpellCheckerInspector.inspectWithRenameTo(
                        manager, method, new TextRange(offsetInParent + start, offsetInParent + end), word
                );
                if (list.size() > 0) {
                    if (problems == null) {
                        problems = new ArrayList<ProblemDescriptor>(list.size());
                    }
                    problems.addAll(list);
                }
                index = end;
            }

            return problems != null ? problems.toArray(new ProblemDescriptor[problems.size()]) : null;
        }
        return null;
    }
}
