package com.gempukku.retro.logic.spawn;

import com.gempukku.secsy.entity.Component;

public interface SpawnerComponent extends Component {
    long getFrequency();

    void setFrequency(long frequency);

    long getLastSpawnTime();

    void setLastSpawnTime(long lastSpawnTime);

    String getPrefab();

    void setPrefab(String prefab);
}
