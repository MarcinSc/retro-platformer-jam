package com.gempukku.secsy.gaming.ai;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.EditableWith;
import com.gempukku.secsy.gaming.editor.component.AINameEditor;

import java.util.Map;

@EditableWith(AINameEditor.class)
public interface AIComponent extends Component {
    String getAiName();

    void setAiName(String aiName);

    Map<String, Object> getValues();

    void setValues(Map<String, Object> values);
}
