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
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.codeStyle.NameUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Local variable name spell checker.
 *
 * @author Alexey Efimov
 */
public class LocalVariableNameSpellCheckerVisitor extends AbstractSpellCheckerVisitor {
    protected LocalVariableNameSpellCheckerVisitor(InspectionManager inspectionManager) {
        super(inspectionManager);
    }

    public void visitLocalVariable(PsiLocalVariable variable) {
        List<ProblemDescriptor> problems = null;
        String variableName = variable.getName();
        PsiIdentifier psiName = variable.getNameIdentifier();
        if (psiName != null && variableName != null) {
            String[] words = NameUtil.nameToWords(variableName);
            int offsetInParent = psiName.getStartOffsetInParent();
            int index = 0;
            for (String word : words) {
                int start = variableName.indexOf(word, index);
                int end = start + word.length();
                List<ProblemDescriptor> list = SpellCheckerInspector.inspectWithRenameTo(
                        inspectionManager, variable, new TextRange(offsetInParent + start, offsetInParent + end), word
                );
                if (list.size() > 0) {
                    if (problems == null) {
                        problems = new ArrayList<ProblemDescriptor>(list.size());
                    }
                    problems.addAll(list);
                }
                index = end;
            }
            if (problems != null) {
                addAll(problems);
            }
        }
    }
}