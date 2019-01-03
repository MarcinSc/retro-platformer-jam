package com.gempukku.secsy.gaming.spawn;

import com.gempukku.secsy.entity.EntityRef;

import java.util.Map;

public interface SpawnManager {
    EntityRef spawnEntity(String prefab);

    EntityRef spawnEntity(String prefab, Map<String, Map<String, Object>> overrides);

    EntityRef spawnEntityAt(String prefab, float x, float y);

    void despawnEntity(EntityRef entityRef);
}
