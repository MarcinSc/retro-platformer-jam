package com.gempukku.retro.logic.combat;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.retro.model.PlayerComponent;
import com.gempukku.retro.render.FadingSpriteComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.faction.FactionManager;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.gempukku.secsy.gaming.physics.basic2d.EntityMoved;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;
import com.gempukku.secsy.gaming.rendering.postprocess.tint.color.ColorTintComponent;
import com.gempukku.secsy.gaming.spawn.SpawnManager;
import com.gempukku.secsy.gaming.time.TimeManager;
import com.gempukku.secsy.gaming.time.delay.DelayManager;
import com.gempukku.secsy.gaming.time.delay.DelayedActionTriggeredEvent;

@RegisterSystem
public class CombatSystem {
    @Inject
    private Basic2dPhysics basic2dPhysics;
    @Inject
    private CameraEntityProvider cameraEntityProvider;
    @Inject
    private GameEntityProvider gameEntityProvider;

    @Inject
    private TimeManager timeManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private DelayManager delayManager;
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private FactionManager factionManager;

    @ReceiveEvent
    public void vulnerableDamaged(SensorContactBegin contact, EntityRef vulnerableEntity, VulnerableComponent vulnerable) {
        if (contact.getSensorType().equals("body")) {
            EntityRef sensorTrigger = contact.getSensorTrigger();
            CausesVulnerabilityComponent cause = sensorTrigger.getComponent(CausesVulnerabilityComponent.class);
            if (cause != null) {
                boolean vulnerableNow = true;
                TemporarilyInvulnerableComponent temporarilyInvulnerable = vulnerableEntity.getComponent(TemporarilyInvulnerableComponent.class);
                if (temporarilyInvulnerable != null) {
                    long time = timeManager.getTime();
                    long effectStart = temporarilyInvulnerable.getEffectStart();
                    long effectDuration = temporarilyInvulnerable.getEffectDuration();
                    if (effectStart <= time && time < effectStart + effectDuration) {
                        vulnerableNow = false;
                    }
                }
                if (vulnerableNow && factionManager.isEnemy(sensorTrigger, vulnerableEntity)) {
                    vulnerableEntity.send(new EntityDamaged(sensorTrigger, null, cause.getDamageAmount()));
                }
            }
        }
    }

    private void spawnDamageSplash(float x, float y) {
        EntityRef damageSplash = spawnManager.spawnEntityAt("damageSplash", x, y);

        FadingSpriteComponent fading = damageSplash.getComponent(FadingSpriteComponent.class);
        fading.setEffectStart(timeManager.getTime());
        long effectDuration = fading.getEffectDuration();

        damageSplash.saveChanges();

        delayManager.addDelayedAction(damageSplash, "destroyEntity", effectDuration);
    }

    @ReceiveEvent
    public void destroyOnCollision(EntityMoved moved, EntityRef entity, DestroyOnCollisionComponent destroy, Position2DComponent position) {
        if (moved.hadCollision()) {
            float x = position.getX();
            float y = position.getY();
            String spawnPrefab = destroy.getSpawnPrefab();

            entityManager.destroyEntity(entity);
            if (spawnPrefab != null) {
                spawnManager.spawnEntityAt(spawnPrefab, x, y);
            }
        }
    }

    @ReceiveEvent
    public void destroyEntity(DelayedActionTriggeredEvent trigger, EntityRef entity) {
        if (trigger.getActionId().equals("destroyEntity")) {
            entityManager.destroyEntity(entity);
        }
    }

    @ReceiveEvent
    public void entityDealtDamage(EntityDamaged entityDamaged, EntityRef entity, HealthComponent health) {
        int currentHealth = health.getCurrentHealth();
        int damage = entityDamaged.getAmount();
        if (damage >= currentHealth) {
            entity.send(new EntityDied());
        } else {
            health.setCurrentHealth(currentHealth - damage);
            entity.saveChanges();
        }
    }

    @ReceiveEvent(priority = -1000)
    public void destroyDead(EntityDied entityDied, EntityRef entity, DestroyOnDeathComponent destroyOnDeath) {
        entityManager.destroyEntity(entity);
    }

    @ReceiveEvent
    public void playerIsDealtDamage(EntityDamaged entityDamaged, EntityRef entity, PlayerComponent player, TemporarilyInvulnerableComponent invulnerable) {
        long time = timeManager.getTime();
        invulnerable.setEffectStart(time);

        entity.saveChanges();

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
