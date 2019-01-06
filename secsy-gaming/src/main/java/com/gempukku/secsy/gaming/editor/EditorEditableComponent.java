package com.gempukku.secsy.gaming.editor;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.DefaultValue;

import java.util.List;

public interface EditorEditableComponent extends Component {
    @DefaultValue("Entity")
    String getNameInEditor();
    void setNameInEditor(String nameInEditor);

    @DefaultValue("true")
    boolean isAddable();
    
    @DefaultValue("true")
    boolean isSelectableInScene();

    List<String> getEditableComponents();
}
