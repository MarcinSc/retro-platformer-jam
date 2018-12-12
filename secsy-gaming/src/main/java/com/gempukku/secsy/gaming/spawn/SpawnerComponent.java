package com.gempukku.secsy.gaming.spawn;

import com.gempukku.secsy.entity.Component;

public interface SpawnerComponent extends Component {
    long getFrequency();

    void setFrequency(long frequency);

    long getLastSpawnTime();

    void setLastSpawnTime(long lastSpawnTime);

    String getPrefab();

    void setPrefab(String prefab);

    float getX();

    void setX(float x);

    float getY();

    void setY(float y);
}
