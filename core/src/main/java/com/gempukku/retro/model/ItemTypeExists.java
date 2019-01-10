package com.gempukku.retro.model;

import com.gempukku.retro.logic.equipment.ItemProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.gaming.editor.generic.type.StringValueValidator;

public class ItemTypeExists implements StringValueValidator {
    @Inject
    private ItemProvider itemProvider;

    @Override
    public boolean isValid(String value) {
        return itemProvider.getItemByName(value) != null;
    }
}
