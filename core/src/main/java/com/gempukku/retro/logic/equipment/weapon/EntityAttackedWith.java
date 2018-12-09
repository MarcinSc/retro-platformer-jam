package com.gempukku.retro.logic.equipment.weapon;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class EntityAttackedWith extends Event {
    private EntityRef attacker;

    public EntityAttackedWith(EntityRef attacker) {
        this.attacker = attacker;
    }

    public EntityRef getAttacker() {
        return attacker;
    }
}
