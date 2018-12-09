package com.gempukku.retro.logic.equipment;

import com.gempukku.secsy.entity.EntityRef;

public interface ItemProvider {
    EntityRef getItemByName(String name);
}
