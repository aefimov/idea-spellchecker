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
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiPlainText;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.xml.XmlComment;

/**
 * PSI visitor to check spelling in comments.
 *
 * @author Sergiy Dubovik
 */
public class CommentsSpellCheckerVisitor extends AbstractSpellCheckerVisitor {
    CommentsSpellCheckerVisitor(InspectionManager inspectionManager) {
        super(inspectionManager);
    }

    public void visitComment(PsiComment comment) {
        String text = comment.getText();
        forEachWord(comment, text);
    }

    public void visitDocComment(PsiDocComment comment) {
        String text = comment.getText();
        forEachWord(comment, text);
    }

    public void visitXmlComment(XmlComment comment) {
        String text = comment.getText();
        forEachWord(comment, text);
    }

    public void visitPlainText(PsiPlainText content) {
        String text = content.getText();
        forEachWord(content, text);
    }
}
