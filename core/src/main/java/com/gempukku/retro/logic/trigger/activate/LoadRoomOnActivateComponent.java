package com.gempukku.retro.logic.trigger.activate;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.EditableWith;

@EditableWith(LoadRoomOnActivateEditor.class)
public interface LoadRoomOnActivateComponent extends Component {
    String getRoomPath();

    void setRoomPath(String roomPath);

    String getSpawnId();

    void setSpawnId(String spawnId);
}
