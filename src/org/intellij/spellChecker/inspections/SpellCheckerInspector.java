package org.intellij.spellChecker.inspections;

import com.intellij.codeInspection.ProblemDescriptor;

/**
 * Spell checker inspector.
 *
 * @author Alexey Efimov
 */
public interface SpellCheckerInspector {
    ProblemDescriptor[] getProblems();
}
