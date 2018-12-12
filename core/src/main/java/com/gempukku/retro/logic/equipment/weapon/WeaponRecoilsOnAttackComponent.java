package com.gempukku.retro.logic.equipment.weapon;

import com.gempukku.secsy.entity.Component;

public interface WeaponRecoilsOnAttackComponent extends Component {
    float getRecoilX();

    float getRecoilY();

    long getRecoilDuration();
}
