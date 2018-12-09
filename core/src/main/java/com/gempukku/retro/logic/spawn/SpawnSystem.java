package com.gempukku.retro.logic.spawn;

import com.gempukku.retro.logic.combat.EntityDied;
import com.gempukku.retro.model.PrefabComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.MovingComponent;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(shared = SpawnManager.class)
public class SpawnSystem extends AbstractLifeCycleSystem implements SpawnManager {
    @Inject
    private EntityManager entityManager;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;

    private EntityIndex spawnerEntities;

    @Override
    public void initialize() {
        spawnerEntities = entityIndexManager.addIndexOnComponents(SpawnerComponent.class);
    }

    @ReceiveEvent
    public void spawnEntities(GameLoopUpdate gameLoop) {
        long time = timeManager.getTime();
        for (EntityRef spawnerEntity : spawnerEntities) {
            SpawnerComponent spawning = spawnerEntity.getComponent(SpawnerComponent.class);
            if (time > spawning.getLastSpawnTime() + spawning.getFrequency()) {
                String prefab = spawning.getPrefab();
                spawning.setLastSpawnTime(time);
                spawnerEntity.saveChanges();

                EntityRef spawned = entityManager.createEntityFromPrefab(prefab);
                Position2DComponent spawnedPosition = spawned.getComponent(Position2DComponent.class);
                if (spawnedPosition != null) {
                    Position2DComponent spawningPosition = spawnerEntity.getComponent(Position2DComponent.class);
                    spawnedPosition.setX(spawningPosition.getX());
                    spawnedPosition.setY(spawningPosition.getY());

                    MovingComponent moving = spawned.getComponent(MovingComponent.class);
                    HorizontalOrientationComponent orientation = spawnerEntity.getComponent(HorizontalOrientationComponent.class);
                    if (moving != null && orientation != null) {
                        float speedX = moving.getSpeedX();
                        moving.setSpeedX(orientation.isFacingRight() ? speedX : -speedX);
                    }

                    spawned.saveChanges();
                }
            }
        }
    }

    @Override
    public EntityRef spawnEntity(String prefab) {
        EntityRef entity = entityManager.createEntityFromPrefab(prefab);

        PrefabComponent prefabComp = entity.createComponent(PrefabComponent.class);
        prefabComp.setPrefab(prefab);

        entity.saveChanges();

        return entity;
    }

    @Override
    public EntityRef spawnEntityAt(String prefab, float x, float y) {
        EntityRef entityRef = spawnEntity(prefab);
        Position2DComponent position = entityRef.getComponent(Position2DComponent.class);
        position.setX(x);
        position.setY(y);

        entityRef.saveChanges();
        return entityRef;
    }

    @ReceiveEvent
    public void spawnOnDeath(EntityDied entityDied, EntityRef entity, SpawnOnDeathComponent spawnOnDeath) {
        Position2DComponent position = entity.getComponent(Position2DComponent.class);
        if (position != null) {
            spawnEntityAt(spawnOnDeath.getPrefab(), position.getX(), position.getY());
        } else {
            spawnEntity(spawnOnDeath.getPrefab());
        }
    }
}
