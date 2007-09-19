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
package org.intellij.spellChecker.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.javadoc.PsiDocComment;
import com.swabunga.spell.engine.Word;
import org.intellij.spellChecker.SpellCheckerManager;
import org.intellij.spellChecker.util.SpellCheckerBundle;
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
public class CommentsSpellCheckerVisitor extends PsiRecursiveElementVisitor {
    private InspectionManager manager;
    private List<ProblemDescriptor> problems = new ArrayList<ProblemDescriptor>();
    @NonNls
    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");

    CommentsSpellCheckerVisitor(InspectionManager manager) {
        this.manager = manager;
    }

    public void visitComment(PsiComment comment) {
        String text = comment.getText();
        forEachWord(comment, text);
    }

    public void visitDocComment(PsiDocComment comment) {
        String text = comment.getText();
        forEachWord(comment, text);
    }

    private void forEachWord(PsiElement element, String text) {
        // Create a pattern to match breaks
        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            visitWord(element, matcher.start(), matcher.end());
        }
    }

    private void visitWord(PsiElement element, int start, int end) {
        if (end - start > 1) {
            TextRange textRange = new TextRange(start, end);
            String word = element.getText().substring(start, end);
            SpellCheckerManager manager = SpellCheckerManager.getInstance();
            List<Word> suggestions = manager.checkWord(word);
            for (Word suggestion : suggestions) {
                problems.add(this.manager.createProblemDescriptor(
                        element, textRange,
                        SpellCheckerBundle.message("word.is.misspelled"),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        new MisspelledQuickFix(textRange, suggestion.getWord())));
            }
        }
    }

    public ProblemDescriptor[] getProblems() {
        return problems.toArray(new ProblemDescriptor[problems.size()]);
    }
}
