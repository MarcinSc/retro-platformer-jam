package com.gempukku.secsy.gaming.ai;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.generic.EditorFields;
import com.gempukku.secsy.gaming.editor.generic.type.EditorField;
import com.gempukku.secsy.gaming.editor.generic.type.StringValidator;

import java.util.Map;

@EditorFields("aiName")
public interface AIComponent extends Component {
    @StringValidator(HasBehaviorValidator.class)
    @EditorField("AI Name")
    String getAiName();

    void setAiName(String aiName);

    Map<String, Object> getValues();

    void setValues(Map<String, Object> values);
}
