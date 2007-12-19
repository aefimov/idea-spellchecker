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
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.intellij.spellChecker.SpellCheckerManager;
import org.intellij.spellChecker.util.SpellCheckerBundle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Spell checker inspector utility.
 *
 * @author Alexey Efimov
 */
public class SpellCheckerInspector {
    @NotNull
    public static List<ProblemDescriptor> inspectWithChangeTo(@NotNull InspectionManager inspectionManager, @NotNull PsiElement element, @NotNull TextRange textRange, @NotNull String word) {
        SpellCheckerManager manager = SpellCheckerManager.getInstance();
        if (manager.hasProblem(word)) {
            List<String> suggestions = manager.getSuggestions(word);
            List<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();
            for (String suggestion : suggestions) {
                fixes.add(new ChangeToQuickFix(textRange, suggestion));
            }
            fixes.add(new AddToDictionaryQuickFix(word));
            fixes.add(new AddToIgnoreListQuickFix(word));
            return Collections.singletonList(inspectionManager.createProblemDescriptor(
                    element, textRange,
                    SpellCheckerBundle.message("word.0.is.misspelled", word),
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    fixes.toArray(new LocalQuickFix[fixes.size()])));
        }
        return Collections.emptyList();
    }

    @NotNull
    public static List<ProblemDescriptor> inspectWithRenameTo(@NotNull InspectionManager inspectionManager, @NotNull PsiElement element, @NotNull TextRange textRange, @NotNull String word) {
        if (word.length() > 1) {
            SpellCheckerManager manager = SpellCheckerManager.getInstance();
            if (manager.hasProblem(word)) {
                PsiElement child = element.findElementAt(textRange.getStartOffset());
                if (child != null) {
                    List<String> suggestions = manager.getSuggestions(word);
                    List<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();
                    for (String suggestion : suggestions) {
                        // Construct corrected method name
                        TextRange subRange = textRange.shiftRight(-child.getStartOffsetInParent());
                        fixes.add(new RenameToQuickFix(subRange.replace(child.getText(), suggestion)));
                    }
                    fixes.add(new AddToDictionaryQuickFix(word));
                    fixes.add(new AddToIgnoreListQuickFix(word));
                    return Collections.singletonList(inspectionManager.createProblemDescriptor(
                            element, textRange,
                            SpellCheckerBundle.message("word.0.is.misspelled", word),
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                            fixes.toArray(new LocalQuickFix[fixes.size()])));
                }
            }
        }
        return Collections.emptyList();
    }
}
