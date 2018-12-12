package com.gempukku.secsy.gaming.spawn;

import com.gempukku.secsy.entity.EntityRef;

public interface SpawnManager {
    EntityRef spawnEntity(String prefab);

    EntityRef spawnEntityAt(String prefab, float x, float y);
}
