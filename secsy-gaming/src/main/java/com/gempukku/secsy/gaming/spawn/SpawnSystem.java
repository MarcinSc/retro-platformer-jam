package com.gempukku.secsy.gaming.spawn;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.entity.io.ComponentData;
import com.gempukku.secsy.entity.io.EntityData;
import com.gempukku.secsy.entity.prefab.PrefabManager;
import com.gempukku.secsy.entity.serialization.ComponentInformation;
import com.gempukku.secsy.entity.serialization.EntityInformation;
import com.gempukku.secsy.entity.serialization.NameComponentManager;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.MovingComponent;
import com.gempukku.secsy.gaming.time.TimeManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RegisterSystem(profiles = "spawn", shared = SpawnManager.class)
public class SpawnSystem extends AbstractLifeCycleSystem implements SpawnManager {
    @Inject
    private EntityManager entityManager;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private NameComponentManager nameComponentManager;

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
                float x = spawning.getX();
                float y = spawning.getY();
                spawning.setLastSpawnTime(time);
                spawnerEntity.saveChanges();

                EntityRef spawned = spawnEntity(prefab);
                Position2DComponent spawnedPosition = spawned.getComponent(Position2DComponent.class);
                if (spawnedPosition != null) {
                    HorizontalOrientationComponent orientation = spawnerEntity.getComponent(HorizontalOrientationComponent.class);
                    Position2DComponent spawningPosition = spawnerEntity.getComponent(Position2DComponent.class);
                    if (orientation != null && !orientation.isFacingRight())
                        x = -x;

                    spawnedPosition.setX(spawningPosition.getX() + x);
                    spawnedPosition.setY(spawningPosition.getY() + y);

                    MovingComponent moving = spawned.getComponent(MovingComponent.class);
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
        return spawnEntity(prefab, null);
    }

    @Override
    public EntityRef spawnEntityAt(String prefab, float x, float y) {
        Map<String, Map<String, Object>> overrides = new HashMap<String, Map<String, Object>>();
        Map<String, Object> positionOverride = new HashMap<String, Object>();
        positionOverride.put("x", x);
        positionOverride.put("y", y);
        overrides.put("Position2D", positionOverride);

        return spawnEntity(prefab, overrides);
    }

    @Override
    public EntityRef spawnEntity(String prefab, Map<String, Map<String, Object>> overrides) {
        Map<String, Map<String, Object>> overrideMap;
        if (overrides == null)
            overrideMap = new HashMap<String, Map<String, Object>>();
        else
            overrideMap = new HashMap<String, Map<String, Object>>(overrides);
        overrideMap.put("Prefab", Collections.<String, Object>singletonMap("prefab", prefab));

        EntityRef entityFromData = entityManager.createEntityFromData(constructEntityData(prefab, overrideMap));
        entityFromData.send(new EntitySpawned(prefab));
        return entityFromData;
    }

    @Override
    public void despawnEntity(EntityRef entityRef) {
        String prefab = entityRef.getComponent(PrefabComponent.class).getPrefab();
        entityRef.send(new EntityDespawning(prefab));
        entityManager.destroyEntity(entityRef);
    }

    private EntityData constructEntityData(String prefab, Map<String, Map<String, Object>> overrideMap) {
        EntityData prefabByName = prefabManager.getPrefabByName(prefab);
        EntityInformation entityInformation = new EntityInformation(prefabByName);
        for (Map.Entry<String, Map<String, Object>> componentOverride : overrideMap.entrySet()) {
            String component = componentOverride.getKey();
            Class<? extends Component> componentClass = nameComponentManager.getComponentByName(component);
            ComponentData componentData = entityInformation.getComponentData(componentClass);
            ComponentInformation componentDataOverride;
            if (componentData == null)
                componentDataOverride = new ComponentInformation(componentClass);
            else
                componentDataOverride = new ComponentInformation(componentData);

            for (Map.Entry<String, Object> componentFieldEntry : componentOverride.getValue().entrySet()) {
                componentDataOverride.addField(componentFieldEntry.getKey(), componentFieldEntry.getValue());
            }

            entityInformation.addComponent(componentDataOverride);
        }
        return entityInformation;
    }
}
