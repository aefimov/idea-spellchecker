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
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPlainText;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.xml.XmlComment;
import org.intellij.spellChecker.util.WordUtils;
import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PSI visition to check spelling in comments.
 *
 * @author Sergiy Dubovik
 */
public class CommentsSpellCheckerVisitor extends AbstractSpellCheckerVisitor {
    private List<ProblemDescriptor> problems = new ArrayList<ProblemDescriptor>();
    @NonNls
    private static final Pattern NON_SPACE = Pattern.compile("\\S+");
    @NonNls
    private static final Pattern WORD = Pattern.compile("\\b\\p{Alpha}+\\b");
    @NonNls
    private static final Pattern URL = Pattern.compile("(https?|ftp|mailto)\\:\\/\\/");
    @NonNls
    private static final Pattern COMPLEX = Pattern.compile("(\\.[^\\.]+)|([@_]+)");

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

    private void forEachWord(PsiElement element, String text) {
        // Create a pattern to match breaks
        Matcher matcher = NON_SPACE.matcher(text);
        while (matcher.find()) {
            visitNonSpace(element, matcher.start(), matcher.end());
        }
    }

    private void visitNonSpace(PsiElement element, int start, int end) {
        if (end - start > 1) {
            String text = element.getText().substring(start, end);
            if (!URL.matcher(text).find() && !COMPLEX.matcher(text).find()) {
                Matcher matcher = WORD.matcher(text);
                while (matcher.find()) {
                    visitWord(element, start + matcher.start(), start + matcher.end());
                }
            }
        }
    }

    private void visitWord(PsiElement element, int start, int end) {
        if (end - start > 1) {
            String word = element.getText().substring(start, end);
            if (!WordUtils.isMixedCase(word)) {
                problems.addAll(inspect(element, new TextRange(start, end), word));
            }
        }
    }

    public ProblemDescriptor[] getProblems() {
        return problems.toArray(new ProblemDescriptor[problems.size()]);
    }
}
