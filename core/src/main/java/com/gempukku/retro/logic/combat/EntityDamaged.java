package com.gempukku.retro.logic.combat;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class EntityDamaged extends Event {
    private EntityRef attacker;
    private EntityRef weapon;
    private int amount;

    public EntityDamaged(EntityRef attacker, EntityRef weapon, int amount) {
        this.attacker = attacker;
        this.weapon = weapon;
        this.amount = amount;
    }

    public EntityRef getAttacker() {
        return attacker;
    }

    public EntityRef getWeapon() {
        return weapon;
    }

    public int getAmount() {
        return amount;
    }
}
