package com.gempukku.retro.logic.equipment;

import com.gempukku.secsy.entity.event.Event;

public class PickedUpObject extends Event {
    private String pickupType;

    public PickedUpObject(String pickupType) {
        this.pickupType = pickupType;
    }

    public String getPickupType() {
        return pickupType;
    }
}
