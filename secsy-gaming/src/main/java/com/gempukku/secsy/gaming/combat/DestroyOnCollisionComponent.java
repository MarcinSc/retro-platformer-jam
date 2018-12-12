package com.gempukku.secsy.gaming.combat;

import com.gempukku.secsy.entity.Component;

public interface DestroyOnCollisionComponent extends Component {
    String getSpawnPrefab();

    void setSpawnPrefab(String spawnPrefab);
}
