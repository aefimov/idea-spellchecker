package org.intellij.spellChecker.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.psi.PsiLiteralExpression;

/**
 * String spell checker.
 *
 * @author Alexey Efimov
 */
public class StringsSpellCheckerVisitor extends AbstractSpellCheckerVisitor {
    protected StringsSpellCheckerVisitor(InspectionManager inspectionManager) {
        super(inspectionManager);
    }

    public void visitLiteralExpression(PsiLiteralExpression expression) {
        String str = expression.getText();
        forEachWord(expression, str);
    }
}
