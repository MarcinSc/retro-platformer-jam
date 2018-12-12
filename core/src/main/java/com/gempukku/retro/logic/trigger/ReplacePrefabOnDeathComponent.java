package com.gempukku.retro.logic.trigger;

import com.gempukku.secsy.entity.Component;

public interface ReplacePrefabOnDeathComponent extends Component {
    String getOldPrefab();

    void setOldPrefab(String oldPrefab);

    String getNewPrefab();

    void setNewPrefab(String newPrefab);
}
