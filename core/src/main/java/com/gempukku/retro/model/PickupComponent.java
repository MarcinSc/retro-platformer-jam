package com.gempukku.retro.model;

import com.gempukku.retro.logic.equipment.PickupEditor;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.EditableWith;

@EditableWith(PickupEditor.class)
public interface PickupComponent extends Component {
    String getType();

    void setType(String type);
}
