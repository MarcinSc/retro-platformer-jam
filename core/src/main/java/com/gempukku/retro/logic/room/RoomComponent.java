package com.gempukku.retro.logic.room;

import com.gempukku.secsy.entity.Component;

public interface RoomComponent extends Component {
    String getRoom();

    void setRoom(String room);

    String getSpawnId();

    void setSpawnId(String spawnId);
}
