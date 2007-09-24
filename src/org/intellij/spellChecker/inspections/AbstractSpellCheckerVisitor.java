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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;

import java.util.List;

/**
 * Base visitor support for spelling.
 *
 * @author Alexey Efimov
 */
public abstract class AbstractSpellCheckerVisitor extends PsiRecursiveElementVisitor implements SpellCheckerVisitor {
    protected InspectionManager inspectionManager;

    protected AbstractSpellCheckerVisitor(InspectionManager inspectionManager) {
        this.inspectionManager = inspectionManager;
    }

    protected List<ProblemDescriptor> inspect(PsiElement element, TextRange textRange, String word) {
        return SpellCheckerInspector.inspectWithChangeTo(inspectionManager, element, textRange, word);
    }
}
