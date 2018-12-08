package com.gempukku.retro.logic.combat;

import com.gempukku.secsy.entity.Component;

public interface CombatComponent extends Component {
    long getLastMeleeAttacked();

    void setLastMeleeAttacked(long lastMeleeAttacked);

    long getMeleeCoolDown();

    void setMeleeCoolDown(long meleeCoolDown);

    int getMeleeDamage();

    void setMeleeDamage(int meleeDamage);
}
