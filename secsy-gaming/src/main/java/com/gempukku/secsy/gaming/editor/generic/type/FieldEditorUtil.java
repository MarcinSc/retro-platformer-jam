package com.gempukku.secsy.gaming.editor.generic.type;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.InternalComponentManager;

import java.lang.reflect.Method;

public class FieldEditorUtil {
    public static String getFieldDisplayName(InternalComponentManager internalComponentManager, Class<? extends Component> componentClass, String fieldName) {
        Method getterMethod = internalComponentManager.getGetterMethod(componentClass, fieldName);
        EditorField editorField = getterMethod.getAnnotation(EditorField.class);

        String displayName;
        if (editorField != null)
            displayName = editorField.value();
        else {
            if (fieldName.length() > 1)
                displayName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            else
                displayName = fieldName.toUpperCase();
        }
        return displayName;
    }
}
