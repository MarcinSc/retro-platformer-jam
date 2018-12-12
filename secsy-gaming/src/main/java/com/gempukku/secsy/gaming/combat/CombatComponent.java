package com.gempukku.secsy.gaming.combat;

import com.gempukku.secsy.entity.Component;

public interface CombatComponent extends Component {
    long getNextAttackTime();

    void setNextAttackTime(long nextAttackTime);
}
