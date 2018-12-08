package com.gempukku.retro.logic.room;

import com.gempukku.secsy.entity.event.Event;

public class LoadRoom extends Event {
    private String roomPath;

    public LoadRoom(String roomPath) {
        this.roomPath = roomPath;
    }

    public String getRoomPath() {
        return roomPath;
    }
}
