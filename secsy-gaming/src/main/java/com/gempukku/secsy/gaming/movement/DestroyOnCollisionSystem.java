package com.gempukku.secsy.gaming.movement;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.EntityMoved;
import com.gempukku.secsy.gaming.spawn.SpawnManager;

@RegisterSystem
public class DestroyOnCollisionSystem {
    @Inject
    private SpawnManager spawnManager;

    @ReceiveEvent
    public void destroyOnCollision(EntityMoved moved, EntityRef entity, DestroyOnCollisionComponent destroy, Position2DComponent position) {
        if (moved.hadCollision()) {
            float x = position.getX();
            float y = position.getY();
            String spawnPrefab = destroy.getSpawnPrefab();

            spawnManager.despawnEntity(entity);
            if (spawnPrefab != null) {
                spawnManager.spawnEntityAt(spawnPrefab, x, y);
            }
        }
    }

}
