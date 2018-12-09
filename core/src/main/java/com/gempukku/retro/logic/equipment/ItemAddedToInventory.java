package com.gempukku.retro.logic.equipment;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class ItemAddedToInventory extends Event {
    private EntityRef owner;
    private String type;

    public ItemAddedToInventory(EntityRef owner, String type) {
        this.owner = owner;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public EntityRef getOwner() {
        return owner;
    }
}
