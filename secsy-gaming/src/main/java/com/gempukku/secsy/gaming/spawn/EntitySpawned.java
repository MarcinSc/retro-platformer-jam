package com.gempukku.secsy.gaming.spawn;

import com.gempukku.secsy.entity.event.Event;

public class EntitySpawned extends Event {
    private String prefab;

    public EntitySpawned(String prefab) {
        this.prefab = prefab;
    }

    public String getPrefab() {
        return prefab;
    }
}
