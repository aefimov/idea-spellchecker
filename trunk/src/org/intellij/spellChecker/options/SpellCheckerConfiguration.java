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
package org.intellij.spellChecker.options;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Spelling configuration.
 *
 * @author Alexey Efimov
 */
@State(
        name = "SpellChecker",
        storages = {
        @Storage(
                id = "spellchecker",
                file = "$APP_CONFIG$/spellchecker.xml"
        )}
)
public final class SpellCheckerConfiguration implements PersistentStateComponent<SpellCheckerConfiguration> {
    public Set<String> USER_DICTIONARY_WORDS = new HashSet<String>();
    public Set<String> IGNORED_WORDS = new HashSet<String>();

    public SpellCheckerConfiguration getState() {
        return this;
    }

    public void loadState(SpellCheckerConfiguration state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
