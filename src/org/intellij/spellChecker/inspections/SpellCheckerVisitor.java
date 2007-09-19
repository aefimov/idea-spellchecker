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
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.SpellChecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergiy Dubovik
 */
public class SpellCheckerVisitor extends PsiRecursiveElementVisitor {
    private InspectionManager manager;
    private List<ProblemDescriptor> problems = new ArrayList<ProblemDescriptor>();
    private SpellChecker spellChecker;
    private String dictFile = "D:\\tools\\jazzy\\english.0";
    private String phonetFile = "D:\\tools\\jazzy\\phonet.en";

    SpellCheckerVisitor(InspectionManager manager) {
        this.manager = manager;
        SpellDictionaryHashMap dict = null;
        try {
            dict = new SpellDictionaryHashMap(new File(dictFile), null);
            spellChecker = new SpellChecker(dict);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void visitComment(PsiComment comment) {
        String text = comment.getText();
        forEachWord(comment, text, comment.getTextRange());
    }

    public void visitDocComment(PsiDocComment comment) {
        String text = comment.getText();
        forEachWord(comment, text, comment.getTextRange());
    }

    private void forEachWord(PsiElement element, String text, TextRange textRange) {
        // Create a pattern to match breaks
        Pattern p = Pattern.compile("\\w+");
        Matcher m = p.matcher(text);
        while (m.find()) {
            System.out.println("Word: " + m.group());
            visitWord(element, m.start(), m.end());
        }
    }

    private void visitWord(PsiElement element, int start, int end) {
        if (end - start > 1) {
            TextRange textRange = new TextRange(start, end);
            List<Word> suggestions = spellChecker.getSuggestions(element.getText().substring(start, end), 10);
            Iterator<Word> iter = suggestions.iterator();
            while (iter.hasNext()) {
                Word suggestion = iter.next();
                problems.add(manager.createProblemDescriptor(element, textRange,
                        "Word is misspelled", ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new MisspelledQuickFix(textRange, suggestion.getWord())));
            }
        }
    }

    public ProblemDescriptor[] getProblems() {
        ProblemDescriptor[] problemArray = new ProblemDescriptor[problems.size()];
        problemArray = problems.toArray(problemArray);
        return problemArray;
    }
}
