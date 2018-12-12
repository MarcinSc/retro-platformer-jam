package com.gempukku.retro.logic.equipment.weapon;

import com.gempukku.secsy.gaming.component.TimedEffectComponent;

public interface RecoilsFromWeaponComponent extends TimedEffectComponent {
    float getRecoilStrengthX();

    void setRecoilStrengthX(float recoilStrengthX);

    float getRecoilStrengthY();

    void setRecoilStrengthY(float recoilStrengthY);
}
