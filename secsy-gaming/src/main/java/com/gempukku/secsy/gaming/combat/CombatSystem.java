package com.gempukku.secsy.gaming.combat;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.faction.FactionManager;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;
import com.gempukku.secsy.gaming.spawn.SpawnManager;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(profiles = "combat")
public class CombatSystem {
    @Inject
    private Basic2dPhysics basic2dPhysics;
    @Inject
    private GameEntityProvider gameEntityProvider;

    @Inject
    private TimeManager timeManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private FactionManager factionManager;

    @ReceiveEvent
    public void vulnerableDamaged(SensorContactBegin contact, EntityRef vulnerableEntity, VulnerableComponent vulnerable) {
        if (contact.getSensorType().equals(vulnerable.getSensorType())) {
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

    @ReceiveEvent
    public void entityDealtDamage(EntityDamaged entityDamaged, EntityRef entity, HealthComponent health) {
        int currentHealth = health.getCurrentHealth();
        int damage = entityDamaged.getAmount();

        int newHealth = currentHealth - damage;
        health.setCurrentHealth(newHealth);
        entity.saveChanges();

        if (newHealth <= 0)
            entity.send(new EntityDied());
    }

    @ReceiveEvent(priority = -1000)
    public void destroyDead(EntityDied entityDied, EntityRef entity, DestroyOnDeathComponent destroyOnDeath) {
        entityManager.destroyEntity(entity);
    }

    @ReceiveEvent
    public void invulnerableUpdate(EntityDamaged entityDamaged, EntityRef entity, TemporarilyInvulnerableComponent invulnerable) {
        long time = timeManager.getTime();
        invulnerable.setEffectStart(time);

        entity.saveChanges();
    }

    @ReceiveEvent
    public void spawnOnDeath(EntityDied entityDied, EntityRef entity, SpawnOnDeathComponent spawnOnDeath) {
        Position2DComponent position = entity.getComponent(Position2DComponent.class);
        if (position != null) {
            spawnManager.spawnEntityAt(spawnOnDeath.getPrefab(), position.getX(), position.getY());
        } else {
            spawnManager.spawnEntity(spawnOnDeath.getPrefab());
        }
    }
}
