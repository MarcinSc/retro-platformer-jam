package com.gempukku.retro.logic.player;

import com.gempukku.secsy.entity.Component;

public interface PlayerSpawnComponent extends Component {
    String getSpawnId();

    void setSpawnId(String spawnId);
}
