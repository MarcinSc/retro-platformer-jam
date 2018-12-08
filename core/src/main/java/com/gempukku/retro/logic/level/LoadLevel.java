package com.gempukku.retro.logic.level;

import com.gempukku.secsy.entity.event.Event;

public class LoadLevel extends Event {
    private String levelPath;

    public LoadLevel(String levelPath) {
        this.levelPath = levelPath;
    }

    public String getLevelPath() {
        return levelPath;
    }
}
