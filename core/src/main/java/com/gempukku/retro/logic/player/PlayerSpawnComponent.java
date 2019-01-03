package com.gempukku.retro.logic.player;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.EditableWith;

@EditableWith(PlayerSpawnEditor.class)
public interface PlayerSpawnComponent extends Component {
    String getSpawnId();

    void setSpawnId(String spawnId);
}
