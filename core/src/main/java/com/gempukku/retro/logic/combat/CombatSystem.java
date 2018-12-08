package com.gempukku.retro.logic.combat;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

@RegisterSystem
public class CombatSystem {
    @Inject
    private Basic2dPhysics basic2dPhysics;

    @ReceiveEvent
    public void entityMeleeAttacked(EntityMeleeAttacked attacked, EntityRef entity, HorizontalOrientationComponent orientation,
                                    CombatComponent combat) {
        boolean facingRight = orientation.isFacingRight();

        String sensor = facingRight ? "attackRight" : "attackLeft";
        for (EntityRef attackedEntity : basic2dPhysics.getContactsForSensor(entity, sensor, new Predicate<EntityRef>() {
            @Override
            public boolean apply(@Nullable EntityRef entityRef) {
                return entityRef.hasComponent(MeleeTargetComponent.class);
            }
        })) {
            System.out.println("Dealt damage: " + combat.getMeleeDamage());
            attackedEntity.send(new DamageDealt(entity, combat.getMeleeDamage()));
        }
    }
}
