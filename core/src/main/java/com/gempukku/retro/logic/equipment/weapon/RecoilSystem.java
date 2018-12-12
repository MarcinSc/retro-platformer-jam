package com.gempukku.retro.logic.equipment.weapon;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.inventory.weapon.WeaponAttack;

@RegisterSystem
public class RecoilSystem {
    @ReceiveEvent
    public void recoilOnAttack(WeaponAttack weaponAttack, EntityRef weapon, RecoilOnAttackComponent recoil) {
        EntityRef attacker = weaponAttack.getAttacker();
        HorizontalOrientationComponent orientation = attacker.getComponent(HorizontalOrientationComponent.class);
        Position2DComponent position = attacker.getComponent(Position2DComponent.class);
        position.setX(position.getX() + recoil.getRecoilX() * (orientation.isFacingRight() ? 1 : -1));
        position.setY(position.getY() + recoil.getRecoilY());
        attacker.saveChanges();
    }
}
