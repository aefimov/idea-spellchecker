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
import org.intellij.spellChecker.util.Strings;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base visitor support for spelling.
 *
 * @author Alexey Efimov
 */
public abstract class AbstractSpellCheckerVisitor extends PsiRecursiveElementVisitor implements SpellCheckerVisitor {
    protected InspectionManager inspectionManager;
    @NonNls
    private static final Pattern NON_SPACE = Pattern.compile("\\S+");
    @NonNls
    private static final Pattern WORD = Pattern.compile("\\b\\p{Alpha}+\\b");
    @NonNls
    private static final Pattern URL = Pattern.compile("(https?|ftp|mailto)\\:\\/\\/");
    @NonNls
    private static final Pattern COMPLEX = Pattern.compile("(\\.[^\\.]+)|([@_]+)");
    private List<ProblemDescriptor> problems;

    protected AbstractSpellCheckerVisitor(InspectionManager inspectionManager) {
        this.inspectionManager = inspectionManager;
    }

    protected List<ProblemDescriptor> inspect(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull String word) {
        return SpellCheckerInspector.inspectWithChangeTo(inspectionManager, element, textRange, word);
    }

    @NotNull
    private static TextRange subRange(@NotNull TextRange range, int start, int end) {
        return TextRange.from(range.getStartOffset() + start, end - start);
    }

    @NotNull
    private static TextRange matcherRange(@NotNull TextRange range, @NotNull Matcher matcher) {
        return subRange(range, matcher.start(), matcher.end());
    }

    protected void forEachWord(@NotNull PsiElement element, @NotNull String text) {
        forEachWord(element, TextRange.from(0, text.length()), text);
    }

    protected void forEachWord(@NotNull PsiElement element, @NotNull TextRange range, @NotNull String text) {
        // Create a pattern to match breaks
        Matcher matcher = NON_SPACE.matcher(text);
        while (matcher.find()) {
            visitNonSpace(element, matcherRange(range, matcher));
        }
    }

    private void visitNonSpace(@NotNull PsiElement element, TextRange range) {
        if (range.getLength() > 1) {
            String text = range.substring(element.getText());
            if (!URL.matcher(text).find() && !COMPLEX.matcher(text).find()) {
                Matcher matcher = WORD.matcher(text);
                while (matcher.find()) {
                    visitWord(element, matcherRange(range, matcher));
                }
            }
        }
    }

    private void visitWord(@NotNull PsiElement element, TextRange range) {
        if (range.getLength() > 1) {
            String word = range.substring(element.getText());
            if (!Strings.isMixedCase(word)) {
                List<ProblemDescriptor> list = inspect(element, range, word);
                if (list.size() > 0) {
                    if (problems == null) {
                        problems = new ArrayList<ProblemDescriptor>(list.size());
                    }
                    problems.addAll(list);
                }
            }
        }
    }

    public ProblemDescriptor[] getProblems() {
        return problems != null ? problems.toArray(new ProblemDescriptor[problems.size()]) : null;
    }
}
