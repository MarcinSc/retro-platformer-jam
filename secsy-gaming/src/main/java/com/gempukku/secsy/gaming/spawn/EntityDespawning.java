package com.gempukku.secsy.gaming.spawn;

import com.gempukku.secsy.entity.event.Event;

public class EntityDespawning extends Event {
    private String prefab;

    public EntityDespawning(String prefab) {
        this.prefab = prefab;
    }

    public String getPrefab() {
        return prefab;
    }
}
