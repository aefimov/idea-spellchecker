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

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.intellij.spellChecker.util.SpellCheckerBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Quick fix for misspelled words.
 *
 * @author Sergiy Dubovik
 */
public class ChangeToQuickFix implements LocalQuickFix {

    private TextRange textRange;

    private String correctWord;

    public ChangeToQuickFix(TextRange textRange, String correctWord) {
        this.textRange = textRange;
        this.correctWord = correctWord;
    }

    @NotNull
    public String getName() {
        return SpellCheckerBundle.message("change.to.0", correctWord);
    }

    @NotNull
    public String getFamilyName() {
        return SpellCheckerBundle.message("change.to");
    }

    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        PsiFile psiFile = descriptor.getPsiElement().getContainingFile();
        Document document = documentManager.getDocument(psiFile);
        int psiElementOffset = descriptor.getPsiElement().getTextOffset();
        if (document != null)
            document.replaceString(
                    psiElementOffset + textRange.getStartOffset(),
                    psiElementOffset + textRange.getEndOffset(),
                    correctWord
            );
    }

}
