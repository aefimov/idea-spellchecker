package org.intellij.spellChecker.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.swabunga.spell.engine.Word;
import org.intellij.spellChecker.SpellCheckerManager;
import org.intellij.spellChecker.util.SpellCheckerBundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base visitor support for spelling.
 *
 * @author Alexey Efimov
 */
public abstract class AbstractSpellCheckerVisitor extends PsiRecursiveElementVisitor implements SpellCheckerInspector {
    protected InspectionManager inspectionManager;

    protected AbstractSpellCheckerVisitor(InspectionManager inspectionManager) {
        this.inspectionManager = inspectionManager;
    }

    protected List<ProblemDescriptor> inspect(PsiElement element, TextRange textRange, String word) {
        SpellCheckerManager manager = SpellCheckerManager.getInstance();
        if (manager.hasProblem(word)) {
            List<Word> suggestions = manager.getSuggestions(word);
            List<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();
            for (Word suggestion : suggestions) {
                fixes.add(new MisspelledQuickFix(textRange, suggestion.getWord()));
            }
            fixes.add(new AddToDictionaryQuickFix(word));
            fixes.add(new IgnoreWordQuickFix(word));
            return Collections.singletonList(inspectionManager.createProblemDescriptor(
                    element, textRange,
                    SpellCheckerBundle.message("word.is.misspelled"),
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    fixes.toArray(new LocalQuickFix[fixes.size()])));
        }
        return Collections.emptyList();
    }
}
