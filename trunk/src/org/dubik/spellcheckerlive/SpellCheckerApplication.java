package org.dubik.spellcheckerlive;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.components.ApplicationComponent;
import org.dubik.spellcheckerlive.inspections.SpellCheckerInspection;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergiy Dubovik
 */
public class SpellCheckerApplication implements ApplicationComponent, InspectionToolProvider {
    public SpellCheckerApplication() {
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "SpellCheckerApplication";
    }

    public Class[] getInspectionClasses() {
        return new Class[]{SpellCheckerInspection.class};
    }
}
