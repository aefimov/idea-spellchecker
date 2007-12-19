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

import com.intellij.codeInspection.InspectionToolProvider;

/**
 * Inspection tool provider.
 *
 * @author Alexey Efimov
 */
public class SpellCheckerInspectionToolProvider implements InspectionToolProvider {
    private static final Class[] INSPECTIONS = {
            CommentsWithMistakesInspection.class,
            ClassNameWithMistakesInspection.class,
            MethodNameWithMistakesInspection.class,
            FieldNameWithMistakesInspection.class,
            LocalVariableNameWithMistakesInspection.class,
            StringWithMistakesInspection.class,
            XmlWithMistakesInspection.class,
            PropertyValueWithMistakesInspection.class,
    };

    public Class[] getInspectionClasses() {
        return INSPECTIONS;
    }
}
