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