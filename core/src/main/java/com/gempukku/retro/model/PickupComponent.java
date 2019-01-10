package com.gempukku.retro.model;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.generic.type.StringValidator;

public interface PickupComponent extends Component {
    @StringValidator(ItemTypeExists.class)
    String getType();

    void setType(String type);
}
