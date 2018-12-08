package com.gempukku.retro.logic.combat;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.retro.model.PlayerComponent;
import com.gempukku.retro.render.FadingSpriteComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;
import com.gempukku.secsy.gaming.rendering.postprocess.tint.color.ColorTintComponent;
import com.gempukku.secsy.gaming.time.TimeManager;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

@RegisterSystem
public class CombatSystem {
    @Inject
    private Basic2dPhysics basic2dPhysics;
    @Inject
    private CameraEntityProvider cameraEntityProvider;
    @Inject
    private TimeManager timeManager;
    @Inject
    private EntityManager entityManager;

    @ReceiveEvent
    public void entityMeleeAttacked(EntityMeleeAttacked attacked, EntityRef entity, HorizontalOrientationComponent orientation,
                                    CombatComponent combat, Position2DComponent attackerPosition) {
        boolean facingRight = orientation.isFacingRight();

        String sensor = facingRight ? "attackRight" : "attackLeft";
        for (EntityRef attackedEntity : basic2dPhysics.getContactsForSensor(entity, sensor, new Predicate<EntityRef>() {
            @Override
            public boolean apply(@Nullable EntityRef entityRef) {
                return entityRef.hasComponent(MeleeTargetComponent.class);
            }
        })) {
            attackedEntity.send(new DamageDealt(entity, combat.getMeleeDamage()));

            float x = attackerPosition.getX() + combat.getMeleeX() * (facingRight ? 1 : -1);
            float y = attackerPosition.getY() + combat.getMeleeY();
            spawnDamageSplash(x, y);
        }
    }

    private void spawnDamageSplash(float x, float y) {
        EntityRef damageSplash = entityManager.createEntityFromPrefab("damageSplash");

        FadingSpriteComponent fading = damageSplash.getComponent(FadingSpriteComponent.class);
        fading.setEffectStart(timeManager.getTime());

        Position2DComponent position = damageSplash.getComponent(Position2DComponent.class);
        position.setX(x);
        position.setY(y);

        damageSplash.saveChanges();
    }

    @ReceiveEvent
    public void entityDealtDamage(DamageDealt damageDealt, EntityRef entity, HealthComponent health) {
        int currentHealth = health.getCurrentHealth();
        int damage = damageDealt.getAmount();
        if (damage >= currentHealth) {
            entity.send(new EntityDied());
        } else {
            health.setCurrentHealth(currentHealth - damage);
            entity.saveChanges();
        }
    }

    @ReceiveEvent
    public void destroyDead(EntityDied entityDied, EntityRef entity, DestroyOnDeathComponent destroyOnDeath) {
        entityManager.destroyEntity(entity);
    }

    @ReceiveEvent
    public void playerIsDealtDamage(DamageDealt damageDealt, EntityRef entity, PlayerComponent player) {
        applyCameraEffect();
    }

    private void applyCameraEffect() {
        EntityRef cameraEntity = cameraEntityProvider.getCameraEntity();
        ColorTintComponent colorTint = cameraEntity.getComponent(ColorTintComponent.class);
        colorTint.setAlpha(new EasedValue(0.3f, "1-0,pow2In"));
        colorTint.setColor(Color.RED);
        colorTint.setEffectStart(timeManager.getTime());
        colorTint.setEffectDuration(500);
        cameraEntity.saveChanges();
    }
}
