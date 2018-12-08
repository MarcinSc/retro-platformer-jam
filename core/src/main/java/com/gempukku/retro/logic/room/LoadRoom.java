package com.gempukku.retro.logic.room;

import com.gempukku.secsy.entity.event.Event;

public class LoadRoom extends Event {
    private String roomPath;
    private float x;
    private float y;

    public LoadRoom(String roomPath, float x, float y) {
        this.roomPath = roomPath;
        this.x = x;
        this.y = y;
    }

    public String getRoomPath() {
        return roomPath;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
