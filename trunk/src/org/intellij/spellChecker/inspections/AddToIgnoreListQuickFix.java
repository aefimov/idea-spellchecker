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

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.actionSystem.Anchor;
import com.intellij.openapi.project.Project;
import org.intellij.spellChecker.SpellCheckerManager;
import org.intellij.spellChecker.util.SpellCheckerBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Ignore word quick fix.
 *
 * @author Sergiy Dubovik
 */
public class AddToIgnoreListQuickFix implements SpellCheckerQuickFix {
    private String word;

    public AddToIgnoreListQuickFix(String word) {
        this.word = word;
    }

    @NotNull
    public String getName() {
        return SpellCheckerBundle.message("add.0.to.ignore.list", word);
    }

    @NotNull
    public String getFamilyName() {
        return SpellCheckerBundle.message("spelling");
    }

    @NotNull
    public Anchor getPopupActionAnchor() {
        return Anchor.LAST;
    }

    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        SpellCheckerManager spellCheckerManager = SpellCheckerManager.getInstance();
        spellCheckerManager.ignoreAll(word);
    }
}
