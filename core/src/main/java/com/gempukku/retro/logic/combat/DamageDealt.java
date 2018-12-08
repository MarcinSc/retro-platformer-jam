package com.gempukku.retro.logic.combat;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class DamageDealt extends Event {
    private EntityRef source;
    private int amount;

    public DamageDealt(EntityRef source, int amount) {
        this.source = source;
        this.amount = amount;
    }

    public EntityRef getSource() {
        return source;
    }

    public int getAmount() {
        return amount;
    }
}
