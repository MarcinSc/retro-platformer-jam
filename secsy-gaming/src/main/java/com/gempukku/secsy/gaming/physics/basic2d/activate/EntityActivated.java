package com.gempukku.secsy.gaming.physics.basic2d.activate;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.event.Event;

public class EntityActivated extends Event {
    private EntityRef source;

    public EntityActivated(EntityRef source) {
        this.source = source;
    }

    public EntityRef getSource() {
        return source;
    }
}
