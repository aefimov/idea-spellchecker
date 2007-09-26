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
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlText;

/**
 * Visits tag text in xml files.
 *
 * @author Sergiy Dubovik
 */
public class AdvancedXmlSpellingVisitor extends AbstractSpellCheckerVisitor {
    protected AdvancedXmlSpellingVisitor(InspectionManager inspectionManager) {
        super(inspectionManager);
    }

    public void visitXmlText(XmlText text) {
        String str = text.getText();
        forEachWord(text, str);
    }

    public void visitXmlAttributeValue(XmlAttributeValue value) {
        String str = value.getValue();
        forEachWord(value, value.getValueTextRange(), str);
    }
}
