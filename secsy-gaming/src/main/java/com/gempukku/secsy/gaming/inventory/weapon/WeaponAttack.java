package com.gempukku.secsy.gaming.inventory.weapon;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class WeaponAttack extends Event {
    private EntityRef attacker;

    public WeaponAttack(EntityRef attacker) {
        this.attacker = attacker;
    }

    public EntityRef getAttacker() {
        return attacker;
    }
}
