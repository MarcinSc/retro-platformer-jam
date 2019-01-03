package com.gempukku.retro.logic.room;

import com.gempukku.secsy.entity.event.Event;

public class LoadRoom extends Event {
    private String roomPath;
    private String spawnId;

    public LoadRoom(String roomPath, String spawnId) {
        this.roomPath = roomPath;
        this.spawnId = spawnId;
    }

    public String getRoomPath() {
        return roomPath;
    }

    public String getSpawnId() {
        return spawnId;
    }
}
