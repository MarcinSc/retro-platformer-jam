package com.gempukku.secsy.gaming.inventory;

import com.gempukku.secsy.entity.EntityRef;

public interface InventoryProvider {
    EntityRef getEquippedItem(EntityRef entity);
}
