package com.gempukku.retro.logic.player;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.generic.EditorName;
import com.gempukku.secsy.gaming.editor.generic.type.EditorField;

@EditorName("Player Spawn")
public interface PlayerSpawnComponent extends Component {
    @EditorField("Id")
    String getSpawnId();

    void setSpawnId(String spawnId);
}
