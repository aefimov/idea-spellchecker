/*
 * Copyright 2006 Sergiy Dubovik
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
package org.dubik.spellcheckerlive.inspections;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergiy Dubovik
 */
public class MisspelledQuickFix implements LocalQuickFix {

    private TextRange textRange;

    private String correctWord;

    public MisspelledQuickFix(TextRange textRange, String correctWord) {
        this.textRange = textRange;
        this.correctWord = correctWord;
    }

    @NotNull
    public String getName() {
        return "Change to " + correctWord;
    }

    @NotNull
    public String getFamilyName() {
        return "Change to";
    }

    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        PsiFile psiFile = descriptor.getPsiElement().getContainingFile();
        Document document = documentManager.getDocument(psiFile);
        int psiElementOffset = descriptor.getPsiElement().getTextOffset();
        if (document != null)
            document.replaceString(psiElementOffset + textRange.getStartOffset(),
                    psiElementOffset + textRange.getEndOffset(), correctWord);
    }

}
