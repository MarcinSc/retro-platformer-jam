package com.gempukku.secsy.gaming.spawn;

import com.gempukku.secsy.entity.Component;

public interface PrefabComponent extends Component {
    String getPrefab();

    void setPrefab(String prefab);
}
