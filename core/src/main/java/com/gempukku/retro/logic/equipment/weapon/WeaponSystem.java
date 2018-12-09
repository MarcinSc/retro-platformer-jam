package com.gempukku.retro.logic.equipment.weapon;

import com.gempukku.retro.logic.combat.CombatComponent;
import com.gempukku.retro.logic.combat.EntityAttacked;
import com.gempukku.retro.logic.combat.EntityDamaged;
import com.gempukku.retro.logic.combat.MeleeTargetComponent;
import com.gempukku.retro.logic.equipment.ItemProvider;
import com.gempukku.retro.model.EquipmentComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.gempukku.secsy.gaming.time.TimeManager;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

@RegisterSystem
public class WeaponSystem {
    @Inject
    private ItemProvider itemProvider;
    @Inject
    private Basic2dPhysics basic2dPhysics;
    @Inject
    private TimeManager timeManager;

    @ReceiveEvent
    public void entityAttacked(EntityAttacked entityAttacked, EntityRef attacker, EquipmentComponent equipmentComponent) {
        String equippedItemName = equipmentComponent.getEquippedItem();
        EntityRef equippedItem = itemProvider.getItemByName(equippedItemName);
        equippedItem.send(new EntityAttackedWith(attacker));
    }

    @ReceiveEvent
    public void attackedWithMeleeWeapon(EntityAttackedWith attackedWith, EntityRef weapon, MeleeWeaponComponent meleeWeapon) {
        EntityRef attacker = attackedWith.getAttacker();
        Position2DComponent position = attacker.getComponent(Position2DComponent.class);
        float x = position.getX();
        float y = position.getY();
        HorizontalOrientationComponent orientation = attacker.getComponent(HorizontalOrientationComponent.class);
        Iterable<EntityRef> attackedEntities;
        if (orientation.isFacingRight()) {
            attackedEntities = basic2dPhysics.getSensorTriggersFor(attacker, "meleeAttack",
                    x + meleeWeapon.getMinimumRange(), x + meleeWeapon.getMaximumRange(),
                    y + meleeWeapon.getMinimumHeight(), y + meleeWeapon.getMaximumHeight(),
                    new Predicate<EntityRef>() {
                        @Override
                        public boolean apply(@Nullable EntityRef entityRef) {
                            return entityRef.hasComponent(MeleeTargetComponent.class);
                        }
                    });
        } else {
            attackedEntities = basic2dPhysics.getSensorTriggersFor(attacker, "meleeAttack",
                    x - meleeWeapon.getMaximumRange(), x - meleeWeapon.getMinimumRange(),
                    y + meleeWeapon.getMinimumHeight(), y + meleeWeapon.getMaximumHeight(),
                    new Predicate<EntityRef>() {
                        @Override
                        public boolean apply(@Nullable EntityRef entityRef) {
                            return entityRef.hasComponent(MeleeTargetComponent.class);
                        }
                    });
        }
        for (EntityRef attackedEntity : attackedEntities)
            attackedEntity.send(new EntityDamaged(attacker, weapon, meleeWeapon.getDamageAmount()));

        CombatComponent combat = attacker.getComponent(CombatComponent.class);
        combat.setNextAttackTime(timeManager.getTime() + meleeWeapon.getCoolDown());
        attacker.saveChanges();
    }
}
