package com.gempukku.retro.model;

import com.gempukku.secsy.entity.Component;

public interface PrefabComponent extends Component {
    String getPrefab();

    void setPrefab(String prefab);
}
