package org.intellij.spellChecker.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.intellij.spellChecker.SpellCheckerManager;
import org.intellij.spellChecker.util.SpellCheckerBundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Spell checker inspector utility.
 *
 * @author Alexey Efimov
 */
public class SpellCheckerInspector {
    public static List<ProblemDescriptor> inspectText(InspectionManager inspectionManager, PsiElement element, TextRange textRange, String word) {
        SpellCheckerManager manager = SpellCheckerManager.getInstance();
        if (manager.hasProblem(word)) {
            List<String> suggestions = manager.getSuggestions(word);
            List<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();
            for (String suggestion : suggestions) {
                fixes.add(new ChangeToQuickFix(textRange, suggestion));
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

    public static List<ProblemDescriptor> inspectPSI(InspectionManager inspectionManager, PsiElement element, TextRange textRange, String word) {
        SpellCheckerManager manager = SpellCheckerManager.getInstance();
        if (manager.hasProblem(word)) {
            List<String> suggestions = manager.getSuggestions(word);
            List<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();
            for (String suggestion : suggestions) {
                fixes.add(new ChangeToQuickFix(textRange, suggestion));
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
