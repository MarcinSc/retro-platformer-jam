package com.gempukku.retro.logic;

import com.gempukku.secsy.entity.event.Event;

public class PickedupItem extends Event {
    private String pickupType;

    public PickedupItem(String pickupType) {
        this.pickupType = pickupType;
    }

    public String getPickupType() {
        return pickupType;
    }
}
