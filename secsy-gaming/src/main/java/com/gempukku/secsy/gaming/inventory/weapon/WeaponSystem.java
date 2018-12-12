package com.gempukku.secsy.gaming.inventory.weapon;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.combat.CombatComponent;
import com.gempukku.secsy.gaming.combat.EntityAttacked;
import com.gempukku.secsy.gaming.combat.EntityDamaged;
import com.gempukku.secsy.gaming.combat.VulnerableComponent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.faction.FactionManager;
import com.gempukku.secsy.gaming.inventory.InventoryProvider;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.gempukku.secsy.gaming.physics.basic2d.MovingComponent;
import com.gempukku.secsy.gaming.spawn.SpawnManager;
import com.gempukku.secsy.gaming.time.TimeManager;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

@RegisterSystem(profiles = "weapon")
public class WeaponSystem {
    @Inject
    private InventoryProvider inventoryProvider;
    @Inject
    private Basic2dPhysics basic2dPhysics;
    @Inject
    private TimeManager timeManager;
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private FactionManager factionManager;

    @ReceiveEvent
    public void entityAttacked(EntityAttacked entityAttacked, EntityRef attacker) {
        EntityRef equippedItem = inventoryProvider.getEquippedItem(attacker);
        equippedItem.send(new WeaponAttack(attacker));
    }

    @ReceiveEvent
    public void attackedWithSpawningWeapon(WeaponAttack attackedWith, EntityRef weapon, SpawningWeaponComponent spawningWeapon) {
        EntityRef attacker = attackedWith.getAttacker();
        HorizontalOrientationComponent orientation = attacker.getComponent(HorizontalOrientationComponent.class);
        Position2DComponent position = attacker.getComponent(Position2DComponent.class);
        float x = position.getX() + spawningWeapon.getX() * (orientation.isFacingRight() ? 1 : -1);
        float y = position.getY() + spawningWeapon.getY();

        String prefab = spawningWeapon.getPrefab();
        EntityRef spawnedEntity = spawnManager.spawnEntityAt(prefab, x, y);
        if (!orientation.isFacingRight()) {
            MovingComponent moving = spawnedEntity.getComponent(MovingComponent.class);
            moving.setSpeedX(-moving.getSpeedX());
            spawnedEntity.saveChanges();
        }

        CombatComponent combat = attacker.getComponent(CombatComponent.class);
        combat.setNextAttackTime(timeManager.getTime() + spawningWeapon.getCoolDown());
        attacker.saveChanges();
    }

    @ReceiveEvent
    public void attackedWithMeleeWeapon(WeaponAttack attackedWith, EntityRef weapon, MeleeWeaponComponent meleeWeapon) {
        EntityRef attacker = attackedWith.getAttacker();
        Position2DComponent position = attacker.getComponent(Position2DComponent.class);
        float x = position.getX();
        float y = position.getY();
        HorizontalOrientationComponent orientation = attacker.getComponent(HorizontalOrientationComponent.class);
        Iterable<EntityRef> attackedEntities;
        float minX;
        float maxX;
        if (orientation.isFacingRight()) {
            minX = x + meleeWeapon.getMinimumRange();
            maxX = x + meleeWeapon.getMaximumRange();
        } else {
            minX = x - meleeWeapon.getMaximumRange();
            maxX = x - meleeWeapon.getMinimumRange();
        }
        attackedEntities = basic2dPhysics.getSensorTriggersFor(attacker, "meleeAttack",
                minX, maxX, y + meleeWeapon.getMinimumHeight(), y + meleeWeapon.getMaximumHeight(),
                new Predicate<EntityRef>() {
                    @Override
                    public boolean apply(@Nullable EntityRef entityRef) {
                        return entityRef.hasComponent(VulnerableComponent.class);
                    }
                });
        for (EntityRef attackedEntity : attackedEntities) {
            if (factionManager.isEnemy(attacker, attackedEntity))
                attackedEntity.send(new EntityDamaged(attacker, weapon, meleeWeapon.getDamageAmount()));
        }

        CombatComponent combat = attacker.getComponent(CombatComponent.class);
        combat.setNextAttackTime(timeManager.getTime() + meleeWeapon.getCoolDown());
        attacker.saveChanges();
    }
}
