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

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.actionSystem.Anchor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringFactory;
import com.intellij.refactoring.RenameRefactoring;
import org.intellij.spellChecker.util.SpellCheckerBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Quick fix for misspelled words (by renaming via refactor menu).
 *
 * @author Alexey Efimov
 */
public class RenameToQuickFix implements SpellCheckerQuickFix {
    private String correctName;

    public RenameToQuickFix(String correctName) {
        this.correctName = correctName;
    }

    @NotNull
    public String getName() {
        return SpellCheckerBundle.message("rename.to.0", correctName);
    }

    @NotNull
    public String getFamilyName() {
        return SpellCheckerBundle.message("rename.to");
    }

    @NotNull
    public Anchor getPopupActionAnchor() {
        return Anchor.FIRST;
    }

    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement psiElement = descriptor.getPsiElement();
        RenameRefactoring rename = RefactoringFactory.getInstance(project).createRename(psiElement, correctName);
        rename.setPreviewUsages(true);
        rename.run();
    }
}