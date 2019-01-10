package com.gempukku.retro.logic.trigger.activate;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.generic.EditorName;
import com.gempukku.secsy.gaming.editor.generic.type.EditorField;
import com.gempukku.secsy.gaming.editor.generic.type.FileExistsValidator;
import com.gempukku.secsy.gaming.editor.generic.type.StringValidator;

@EditorName("Activated Teleport")
public interface LoadRoomOnActivateComponent extends Component {
    @EditorField("Room Path")
    @StringValidator(FileExistsValidator.class)
    String getRoomPath();

    void setRoomPath(String roomPath);

    @EditorField("Spawn Id")
    String getSpawnId();

    void setSpawnId(String spawnId);
}
