package com.gempukku.retro.logic.equipment.weapon;

import com.gempukku.secsy.entity.Component;

public interface MeleeWeaponComponent extends Component {
    int getDamageAmount();

    long getCoolDown();

    float getMinimumRange();

    float getMaximumRange();

    float getMinimumHeight();

    float getMaximumHeight();
}
