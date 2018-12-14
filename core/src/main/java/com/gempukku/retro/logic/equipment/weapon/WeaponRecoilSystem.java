package com.gempukku.retro.logic.equipment.weapon;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.inventory.weapon.WeaponAttack;
import com.gempukku.secsy.gaming.physics.basic2d.GatherAcceleration;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem
public class WeaponRecoilSystem {
    @Inject
    private TimeManager timeManager;

    @ReceiveEvent
    public void recoilOnAttack(WeaponAttack weaponAttack, EntityRef weapon, WeaponRecoilsOnAttackComponent recoil) {
        EntityRef attacker = weaponAttack.getAttacker();
        HorizontalOrientationComponent orientation = attacker.getComponent(HorizontalOrientationComponent.class);
        RecoilsFromWeaponComponent weaponRecoil = attacker.getComponent(RecoilsFromWeaponComponent.class);
        if (weaponRecoil != null) {
            long time = timeManager.getTime();
            long duration = recoil.getRecoilDuration();

            weaponRecoil.setEffectStart(time);
            weaponRecoil.setEffectDuration(duration);
            weaponRecoil.setRecoilStrengthX(recoil.getRecoilX() * (orientation.isFacingRight() ? 1 : -1));
            weaponRecoil.setRecoilStrengthY(recoil.getRecoilY());

            attacker.saveChanges();
        }
    }

    @ReceiveEvent
    public void recoilEntity(GatherAcceleration acceleration, EntityRef entity, RecoilsFromWeaponComponent recoilsFromWeapon) {
        long timeSinceRecoil = timeManager.getTime() - recoilsFromWeapon.getEffectStart();
        long duration = recoilsFromWeapon.getEffectDuration();
        if (timeSinceRecoil < duration) {
            float perc = 1f - 1f * timeSinceRecoil / duration;
            acceleration.addAcceleration(recoilsFromWeapon.getRecoilStrengthX() * perc, recoilsFromWeapon.getRecoilStrengthY() * perc);
        }
    }
}
